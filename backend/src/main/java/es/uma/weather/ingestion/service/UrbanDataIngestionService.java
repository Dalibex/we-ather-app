package es.uma.weather.ingestion.service;

import es.uma.weather.heatmap.config.HeatIngestionProperties;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Service
public class UrbanDataIngestionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrbanDataIngestionService.class);
    private final RestClient restClient;
    private final JsonMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;
    private final HeatIngestionProperties properties;

    public UrbanDataIngestionService(
            RestClient.Builder restClientBuilder,
            JsonMapper objectMapper,
            JdbcTemplate jdbcTemplate,
            HeatIngestionProperties properties) {
        this.restClient = restClientBuilder.build();
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
        this.properties = properties;
    }

    public void ingest() {
        LOGGER.info("Iniciando ingesta de calles de OpenStreetMap");
        String query =
                "[out:json][timeout:60];way[\"highway\"]("
                        + properties.overpassBbox()
                        + ");out geom;";
        String form = "data=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
        String response =
                restClient
                        .post()
                        .uri(properties.overpassUrl())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .body(form)
                        .retrieve()
                        .body(String.class);

        try {
            JsonNode elements = objectMapper.readTree(response).path("elements");
            int saved = 0;
            for (JsonNode element : elements) {
                if (!"way".equals(element.path("type").asString())) continue;
                JsonNode geometry = element.path("geometry");
                if (!geometry.isArray() || geometry.size() < 2) continue;
                String wkt = toLineString(geometry);
                if (wkt == null) continue;
                long osmId = element.path("id").asLong();
                String highway = element.path("tags").path("highway").asString("unknown");
                String name = element.path("tags").path("name").asString(null);
                jdbcTemplate.update(
                        """
                        INSERT INTO street_segments (osm_way_id, highway, name, geom, updated_at)
                        VALUES (?, ?, ?, ST_GeomFromText(?, 4326), NOW())
                        ON CONFLICT (osm_way_id) DO UPDATE SET
                            highway = EXCLUDED.highway,
                            name = EXCLUDED.name,
                            geom = EXCLUDED.geom,
                            updated_at = NOW()
                        """,
                        osmId,
                        highway,
                        name,
                        wkt);
                saved++;
            }
            LOGGER.info("Ingesta de OpenStreetMap terminada: {} tramos guardados", saved);
        } catch (DataAccessException | JacksonException exception) {
            throw new IllegalStateException(
                    "No se pudo procesar la respuesta de Overpass", exception);
        }
    }

    private String toLineString(JsonNode geometry) {
        StringBuilder wkt = new StringBuilder("LINESTRING(");
        for (int i = 0; i < geometry.size(); i++) {
            JsonNode point = geometry.get(i);
            double latitude = point.path("lat").asDouble(Double.NaN);
            double longitude = point.path("lon").asDouble(Double.NaN);
            if (!Double.isFinite(latitude)
                    || !Double.isFinite(longitude)
                    || latitude < -90
                    || latitude > 90
                    || longitude < -180
                    || longitude > 180) {
                return null;
            }
            if (i > 0) wkt.append(',');
            wkt.append(longitude).append(' ').append(latitude);
        }
        return wkt.append(')').toString();
    }
}
