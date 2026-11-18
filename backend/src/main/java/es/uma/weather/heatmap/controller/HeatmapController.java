package es.uma.weather.heatmap.controller;

import es.uma.weather.heatmap.dto.HeatCellDto;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/heatmap")
public class HeatmapController {

    private final JdbcTemplate jdbcTemplate;

    public HeatmapController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public List<HeatCellDto> getLatestHeatmap(
            @RequestParam(defaultValue = "36.68") double south,
            @RequestParam(defaultValue = "36.78") double north,
            @RequestParam(defaultValue = "-4.52") double west,
            @RequestParam(defaultValue = "-4.35") double east) {
        if (south >= north
                || west >= east
                || south < -90
                || north > 90
                || west < -180
                || east > 180) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Bounding box geográfico no válido");
        }
        return jdbcTemplate.query(
                """
                SELECT DISTINCT ON (latitude, longitude)
                    grid_name, latitude, longitude, temperature_c,
                    relative_humidity_pct, wind_speed_kmh,
                    shortwave_radiation_wm2, uv_index, heat_score,
                    heat_level, observed_at
                FROM weather_observations
                WHERE latitude BETWEEN ? AND ?
                  AND longitude BETWEEN ? AND ?
                ORDER BY latitude, longitude, observed_at DESC
                """,
                (rs, rowNum) ->
                        new HeatCellDto(
                                rs.getString("grid_name"),
                                rs.getDouble("latitude"),
                                rs.getDouble("longitude"),
                                rs.getDouble("temperature_c"),
                                rs.getDouble("relative_humidity_pct"),
                                rs.getDouble("wind_speed_kmh"),
                                rs.getDouble("shortwave_radiation_wm2"),
                                rs.getDouble("uv_index"),
                                rs.getDouble("heat_score"),
                                rs.getString("heat_level"),
                                rs.getTimestamp("observed_at").toInstant()),
                south,
                north,
                west,
                east);
    }
}
