package es.uma.weather.heatmap.service;

import es.uma.weather.heatmap.config.HeatIngestionProperties;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionOperations;

/** Genera el mapa de calor interno por zona y lo guarda en {@code zone_heat}. */
// shortcut: supone una sola instancia del servidor; con varias, cada una regeneraría el mapa (sin
// daño, pero repetido). Si la aplicación escala, añadir un bloqueo compartido como ShedLock.
@Service
public class HeatmapGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeatmapGenerator.class);

    private static final String LATEST_OBSERVATION_SQL =
            """
            SELECT heat_score, observed_at
            FROM weather_observations
            WHERE source = 'OPEN_METEO' AND latitude = ? AND longitude = ?
            ORDER BY observed_at DESC
            LIMIT 1
            """;

    private static final String DELETE_ZONES_SQL = "DELETE FROM zone_heat";

    private static final String INSERT_ZONE_SQL =
            """
            INSERT INTO zone_heat (zone_name, latitude, longitude, heat_score, observed_at,
                                   generated_at)
            VALUES (?, ?, ?, ?, ?, NOW())
            """;

    private final JdbcTemplate jdbcTemplate;
    private final TransactionOperations transactionOperations;
    private final HeatIngestionProperties properties;
    private final ReportCombiner reportCombiner;
    private final ValidatedReportSource reportSource;

    public HeatmapGenerator(
            JdbcTemplate jdbcTemplate,
            TransactionOperations transactionOperations,
            HeatIngestionProperties properties,
            ReportCombiner reportCombiner,
            ValidatedReportSource reportSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionOperations = transactionOperations;
        this.properties = properties;
        this.reportCombiner = reportCombiner;
        this.reportSource = reportSource;
    }

    /**
     * Sustituye el mapa de calor guardado por uno nuevo y devuelve cuántas zonas guarda.
     *
     * @return el número de zonas guardadas; 0 si ninguna tiene observación, y entonces se conserva
     *     el mapa anterior
     */
    public int generate() {
        List<ZoneHeat> zones = new ArrayList<>();
        // Por nombre, no por posición: así no depende del orden en que combine() devuelve las
        // zonas.
        Map<String, OffsetDateTime> observedAtByZone = new HashMap<>();
        for (HeatIngestionProperties.GridPoint point : properties.points()) {
            List<Observation> latest =
                    jdbcTemplate.query(
                            LATEST_OBSERVATION_SQL,
                            (rs, rowNum) ->
                                    new Observation(
                                            rs.getDouble("heat_score"),
                                            rs.getObject("observed_at", OffsetDateTime.class)),
                            point.latitude(),
                            point.longitude());
            if (latest.isEmpty()) continue;
            zones.add(
                    new ZoneHeat(
                            point.name(),
                            point.latitude(),
                            point.longitude(),
                            latest.getFirst().heatScore()));
            observedAtByZone.put(point.name(), latest.getFirst().observedAt());
        }
        if (zones.isEmpty()) {
            LOGGER.warn(
                    "Ninguna zona tiene observación de Open-Meteo: se conserva el mapa anterior");
            return 0;
        }

        // Las lecturas quedan fuera de la transacción; dentro solo van las escrituras.
        List<ZoneHeat> combined =
                reportCombiner.combine(zones, reportSource.currentValidatedReports());
        transactionOperations.executeWithoutResult(
                status -> {
                    jdbcTemplate.update(DELETE_ZONES_SQL);
                    for (ZoneHeat zone : combined) {
                        jdbcTemplate.update(
                                INSERT_ZONE_SQL,
                                zone.name(),
                                zone.latitude(),
                                zone.longitude(),
                                zone.heatScore(),
                                observedAtByZone.get(zone.name()));
                    }
                });
        LOGGER.info("Mapa de calor generado: {} zonas guardadas", combined.size());
        return combined.size();
    }

    /** La última observación de Open-Meteo de un punto. */
    private record Observation(double heatScore, OffsetDateTime observedAt) {}
}
