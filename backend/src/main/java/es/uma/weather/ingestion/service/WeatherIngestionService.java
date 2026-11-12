package es.uma.weather.ingestion.service;

import es.uma.weather.heatmap.config.HeatIngestionProperties;
import es.uma.weather.heatmap.config.HeatIngestionProperties.GridPoint;
import es.uma.weather.heatmap.service.HeatScoreCalculator;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Service
public class WeatherIngestionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherIngestionService.class);
    private static final String SOURCE = "OPEN_METEO";

    private final RestClient restClient;
    private final JsonMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;
    private final HeatIngestionProperties properties;
    private final HeatScoreCalculator heatScoreCalculator;

    public WeatherIngestionService(
            RestClient.Builder restClientBuilder,
            JsonMapper objectMapper,
            JdbcTemplate jdbcTemplate,
            HeatIngestionProperties properties,
            HeatScoreCalculator heatScoreCalculator) {
        this.restClient = restClientBuilder.build();
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
        this.properties = properties;
        this.heatScoreCalculator = heatScoreCalculator;
    }

    public void ingest() {
        LOGGER.info("Iniciando ingesta meteorológica de {} puntos", properties.points().size());
        int successful = 0;
        for (GridPoint point : properties.points()) {
            try {
                ingestPoint(point);
                successful++;
            } catch (Exception exception) {
                LOGGER.warn(
                        "No se pudo actualizar el punto {} ({}, {}): {}",
                        point.name(),
                        point.latitude(),
                        point.longitude(),
                        exception.getMessage());
            }
        }
        LOGGER.info(
                "Ingesta meteorológica terminada: {}/{} puntos actualizados",
                successful,
                properties.points().size());
    }

    private void ingestPoint(GridPoint point) throws Exception {
        URI requestUri =
                UriComponentsBuilder.fromUriString(properties.openMeteoUrl())
                        .queryParam("latitude", point.latitude())
                        .queryParam("longitude", point.longitude())
                        .queryParam("current", "temperature_2m,relative_humidity_2m,wind_speed_10m")
                        .queryParam("hourly", "shortwave_radiation,uv_index")
                        .queryParam("timezone", "Europe/Madrid")
                        .build()
                        .toUri();
        String body = restClient.get().uri(requestUri).retrieve().body(String.class);

        JsonNode root = objectMapper.readTree(body);
        JsonNode current = root.path("current");
        if (current.isMissingNode() || current.path("temperature_2m").isMissingNode()) {
            throw new IllegalStateException("La respuesta no contiene datos current válidos");
        }

        double temperature = current.path("temperature_2m").asDouble();
        double humidity = current.path("relative_humidity_2m").asDouble();
        double wind = current.path("wind_speed_10m").asDouble();
        int utcOffset = root.path("utc_offset_seconds").asInt(0);
        LocalDateTime localTime = LocalDateTime.parse(current.path("time").asString());
        int hourlyIndex = findHourlyIndex(root.path("hourly").path("time"), localTime);
        JsonNode hourly = root.path("hourly");
        double radiation = hourly.path("shortwave_radiation").path(hourlyIndex).asDouble(0.0);
        double uv = hourly.path("uv_index").path(hourlyIndex).asDouble(0.0);
        var observedAt = localTime.atOffset(ZoneOffset.ofTotalSeconds(utcOffset)).toInstant();
        double score = heatScoreCalculator.calculate(temperature, humidity, wind, radiation);
        String level = heatScoreCalculator.level(score);

        jdbcTemplate.update(
                """
                INSERT INTO weather_observations
                    (source, grid_name, latitude, longitude, observed_at,
                     temperature_c, relative_humidity_pct, wind_speed_kmh,
                     shortwave_radiation_wm2, uv_index, heat_score, heat_level, location)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                        ST_SetSRID(ST_MakePoint(?, ?), 4326)::geography)
                ON CONFLICT (source, latitude, longitude, observed_at)
                DO UPDATE SET
                    temperature_c = EXCLUDED.temperature_c,
                    relative_humidity_pct = EXCLUDED.relative_humidity_pct,
                    wind_speed_kmh = EXCLUDED.wind_speed_kmh,
                    shortwave_radiation_wm2 = EXCLUDED.shortwave_radiation_wm2,
                    uv_index = EXCLUDED.uv_index,
                    heat_score = EXCLUDED.heat_score,
                    heat_level = EXCLUDED.heat_level,
                    location = EXCLUDED.location,
                    ingested_at = NOW()
                """,
                SOURCE,
                point.name(),
                point.latitude(),
                point.longitude(),
                observedAt,
                temperature,
                humidity,
                wind,
                radiation,
                uv,
                score,
                level,
                point.longitude(),
                point.latitude());

        LOGGER.debug("Punto {} guardado con exposición {} ({})", point.name(), score, level);
    }

    private int findHourlyIndex(JsonNode times, LocalDateTime currentTime) {
        int selected = 0;
        LocalDateTime selectedTime = null;
        for (int i = 0; i < times.size(); i++) {
            LocalDateTime candidate = LocalDateTime.parse(times.get(i).asString());
            if (!candidate.isAfter(currentTime)
                    && (selectedTime == null || candidate.isAfter(selectedTime))) {
                selected = i;
                selectedTime = candidate;
            }
        }
        if (selectedTime == null) {
            throw new IllegalStateException(
                    "Open-Meteo no ha devuelto una hora meteorológica válida");
        }
        return selected;
    }
}
