package es.uma.weather.ingestion.service;

import es.uma.weather.heatmap.config.HeatIngestionProperties;
import es.uma.weather.heatmap.config.HeatIngestionProperties.GridPoint;
import es.uma.weather.heatmap.service.HeatScoreCalculator;
import es.uma.weather.shared.proveedores.LimitadorPeticiones;
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
    private final LimitadorPeticiones limitadorPeticiones;

    public WeatherIngestionService(
            RestClient.Builder restClientBuilder,
            JsonMapper objectMapper,
            JdbcTemplate jdbcTemplate,
            HeatIngestionProperties properties,
            HeatScoreCalculator heatScoreCalculator,
            LimitadorPeticiones limitadorPeticiones) {
        this.restClient = restClientBuilder.build();
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
        this.properties = properties;
        this.heatScoreCalculator = heatScoreCalculator;
        this.limitadorPeticiones = limitadorPeticiones;
    }

    public int ingest() {
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
        return successful;
    }

    private void ingestPoint(GridPoint point) throws Exception {
        if (!limitadorPeticiones.permitir("open-meteo")) {
            throw new IllegalStateException("Límite de peticiones a Open-Meteo alcanzado");
        }
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
        double temperature = requireNumber(current, "temperature_2m");
        double humidity = requireNumber(current, "relative_humidity_2m");
        double wind = requireNumber(current, "wind_speed_10m");
        JsonNode offsetNode = root.path("utc_offset_seconds");
        JsonNode timeNode = current.path("time");
        if (!offsetNode.isNumber() || !timeNode.isString()) {
            throw new IllegalStateException("Open-Meteo no ha devuelto la hora de la observación");
        }
        int utcOffset = offsetNode.asInt();
        LocalDateTime localTime = LocalDateTime.parse(timeNode.asString());
        JsonNode hourly = root.path("hourly");
        int hourlyIndex = findHourlyIndex(hourly.path("time"), localTime);
        double radiation =
                requireNumber(
                        hourly.path("shortwave_radiation"), hourlyIndex, "shortwave_radiation");
        double uv = requireNumber(hourly.path("uv_index"), hourlyIndex, "uv_index");
        // OffsetDateTime, not Instant: the PostgreSQL driver cannot bind an Instant.
        var observedAt = localTime.atOffset(ZoneOffset.ofTotalSeconds(utcOffset));
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

    private static double requireNumber(JsonNode node, String field) {
        JsonNode value = node.path(field);
        if (!value.isNumber()) {
            throw new IllegalStateException(
                    "Open-Meteo no ha devuelto un valor numérico en " + field);
        }
        return value.asDouble();
    }

    private static double requireNumber(JsonNode array, int index, String field) {
        JsonNode value = array.path(index);
        if (!value.isNumber()) {
            throw new IllegalStateException(
                    "Open-Meteo no ha devuelto un valor numérico en " + field);
        }
        return value.asDouble();
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
