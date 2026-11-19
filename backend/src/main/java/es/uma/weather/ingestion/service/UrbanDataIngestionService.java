package es.uma.weather.ingestion.service;

import es.uma.weather.heatmap.config.HeatIngestionProperties;
import es.uma.weather.shared.proveedores.LimitadorPeticiones;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Service
public class UrbanDataIngestionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrbanDataIngestionService.class);

    private static final String UPSERT_SEGMENT_SQL =
            """
            INSERT INTO street_segments (osm_way_id, highway, name, geom, updated_at)
            VALUES (?, ?, ?, ST_GeomFromText(?, 4326), NOW())
            ON CONFLICT (osm_way_id) DO UPDATE SET
                highway = EXCLUDED.highway,
                name = EXCLUDED.name,
                geom = EXCLUDED.geom,
                updated_at = NOW()
            """;

    /** Retira los tramos cuya vía no ha llegado en la carga: ya no está en OpenStreetMap. */
    private static final String DELETE_MISSING_SEGMENTS_SQL =
            "DELETE FROM street_segments WHERE osm_way_id <> ALL (?)";

    /** El reloj es el de la base de datos; sin tramos guardados cuenta como desactualizado. */
    private static final String IS_OUTDATED_SQL =
            "SELECT COALESCE(MAX(updated_at) < NOW() - INTERVAL '7 days', TRUE) FROM street_segments";

    private final RestClient restClient;
    private final JsonMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;
    private final TransactionOperations transactionOperations;
    private final HeatIngestionProperties properties;
    private final LimitadorPeticiones limitadorPeticiones;

    public UrbanDataIngestionService(
            RestClient.Builder restClientBuilder,
            JsonMapper objectMapper,
            JdbcTemplate jdbcTemplate,
            TransactionOperations transactionOperations,
            HeatIngestionProperties properties,
            LimitadorPeticiones limitadorPeticiones) {
        this.restClient =
                restClientBuilder.defaultHeader("User-Agent", properties.userAgent()).build();
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
        this.transactionOperations = transactionOperations;
        this.properties = properties;
        this.limitadorPeticiones = limitadorPeticiones;
    }

    /**
     * Sustituye las calles guardadas por las que devuelve Overpass.
     *
     * <p>Solo una respuesta completa y con vías cambia la tabla, y lo hace entera o nada: en una
     * transacción guarda todas las vías y retira las que ya no están en OpenStreetMap. En cualquier
     * otro caso, también si Overpass o la base de datos fallan, se conservan las calles anteriores.
     *
     * @return tramos guardados, o 0 si no se ha actualizado nada
     */
    public int ingest() {
        LOGGER.info("Iniciando ingesta de calles de OpenStreetMap");
        if (!limitadorPeticiones.permitir("overpass")) {
            LOGGER.warn("Límite de peticiones a Overpass alcanzado: no se actualizan las calles");
            return 0;
        }
        try {
            return replaceWith(objectMapper.readTree(fetchStreets()));
        } catch (RestClientException
                | JacksonException
                | DataAccessException
                | TransactionException exception) {
            LOGGER.warn(
                    "No se pudieron actualizar las calles de Overpass: se conservan las"
                            + " anteriores",
                    exception);
            return 0;
        }
    }

    private int replaceWith(JsonNode response) {
        // Si la consulta se queda sin tiempo o sin memoria, Overpass responde 200 igualmente, con
        // «remark» y solo parte de las vías.
        String remark = response.path("remark").asString("");
        if (!remark.isBlank()) {
            LOGGER.warn(
                    "Overpass ha devuelto una respuesta incompleta ({}): se conservan las calles"
                            + " anteriores",
                    remark);
            return 0;
        }
        List<StreetSegment> segments = toSegments(response.path("elements"));
        // Una carga sin vías nunca es Málaga, y retirar las que faltan las borraría todas.
        if (segments.isEmpty()) {
            LOGGER.warn(
                    "Overpass no ha devuelto ninguna vía válida: se conservan las calles anteriores");
            return 0;
        }

        // La petición a Overpass queda fuera de la transacción, para no retener una conexión a la
        // base de datos mientras se espera su respuesta.
        transactionOperations.executeWithoutResult(status -> replaceSegments(segments));
        LOGGER.info("Ingesta de OpenStreetMap terminada: {} tramos guardados", segments.size());
        return segments.size();
    }

    private String fetchStreets() {
        String query =
                "[out:json][timeout:60];way[\"highway\"]("
                        + properties.overpassBbox()
                        + ");out geom;";
        String form = "data=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
        return restClient
                .post()
                .uri(properties.overpassUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(String.class);
    }

    private List<StreetSegment> toSegments(JsonNode elements) {
        List<StreetSegment> segments = new ArrayList<>();
        for (JsonNode element : elements) {
            if (!"way".equals(element.path("type").asString())) continue;
            JsonNode geometry = element.path("geometry");
            if (!geometry.isArray() || geometry.size() < 2) continue;
            String wkt = toLineString(geometry);
            if (wkt == null) continue;
            segments.add(
                    new StreetSegment(
                            element.path("id").asLong(),
                            element.path("tags").path("highway").asString("unknown"),
                            element.path("tags").path("name").asString(null),
                            wkt));
        }
        return segments;
    }

    private void replaceSegments(List<StreetSegment> segments) {
        for (StreetSegment segment : segments) {
            jdbcTemplate.update(
                    UPSERT_SEGMENT_SQL,
                    segment.osmWayId(),
                    segment.highway(),
                    segment.name(),
                    segment.wkt());
        }
        Long[] osmWayIds = segments.stream().map(StreetSegment::osmWayId).toArray(Long[]::new);
        int retired =
                jdbcTemplate.update(
                        DELETE_MISSING_SEGMENTS_SQL,
                        statement ->
                                statement.setArray(
                                        1,
                                        statement
                                                .getConnection()
                                                .createArrayOf("bigint", osmWayIds)));
        LOGGER.info("Retirados {} tramos que ya no están en OpenStreetMap", retired);
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

    /**
     * Indica si hay que volver a cargar las calles: no hay ninguna guardada o la última se
     * actualizó hace más de siete días.
     */
    public boolean isOutdated() {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(IS_OUTDATED_SQL, Boolean.class));
    }

    /** Un tramo de calle leído de Overpass, listo para guardarse. */
    private record StreetSegment(long osmWayId, String highway, String name, String wkt) {}
}
