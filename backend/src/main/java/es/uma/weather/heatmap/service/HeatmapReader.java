package es.uma.weather.heatmap.service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Entrega al motor de rutas el último mapa de calor interno que guardó {@link HeatmapGenerator}.
 * Nunca llama a los proveedores: solo lee {@code zone_heat}.
 */
@Service
public class HeatmapReader {

    private static final String ZONES_SQL =
            """
            SELECT zone_name, latitude, longitude, heat_score, observed_at, generated_at
            FROM zone_heat
            ORDER BY zone_name
            """;

    private final JdbcTemplate jdbcTemplate;

    public HeatmapReader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * El último mapa de calor generado, sea cual sea su antigüedad: se usa siempre el último dato
     * válido.
     *
     * @return el mapa, o vacío si todavía no se ha generado ninguno
     */
    public Optional<HeatmapSnapshot> latest() {
        // Una sola consulta: la generación sustituye la tabla en una transacción, así que no se
        // pueden mezclar zonas de dos generaciones.
        List<Row> rows =
                jdbcTemplate.query(
                        ZONES_SQL,
                        (rs, rowNum) ->
                                new Row(
                                        new ZoneHeat(
                                                rs.getString("zone_name"),
                                                rs.getDouble("latitude"),
                                                rs.getDouble("longitude"),
                                                rs.getDouble("heat_score")),
                                        rs.getObject("observed_at", OffsetDateTime.class)
                                                .toInstant(),
                                        rs.getObject("generated_at", OffsetDateTime.class)
                                                .toInstant()));
        if (rows.isEmpty()) {
            return Optional.empty();
        }
        Instant observedAt = rows.stream().map(Row::observedAt).min(Instant::compareTo).get();
        Instant generatedAt = rows.stream().map(Row::generatedAt).max(Instant::compareTo).get();
        return Optional.of(
                new HeatmapSnapshot(
                        rows.stream().map(Row::zone).toList(), generatedAt, observedAt));
    }

    private record Row(ZoneHeat zone, Instant observedAt, Instant generatedAt) {}
}
