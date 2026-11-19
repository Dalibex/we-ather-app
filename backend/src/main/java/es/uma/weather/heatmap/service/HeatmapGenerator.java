package es.uma.weather.heatmap.service;

import es.uma.weather.heatmap.config.HeatIngestionProperties;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionOperations;

/** Genera el mapa de calor interno por zona y lo guarda en {@code zone_heat}. */
// shortcut: assumes a single server instance; with several, each would regenerate the snapshot
// (harmless but redundant), add a lock such as ShedLock if the app scales out.
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
        List<OffsetDateTime> observedAts = new ArrayList<>();
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
            observedAts.add(latest.getFirst().observedAt());
        }
        if (zones.isEmpty()) {
            LOGGER.warn(
                    "Ninguna zona tiene observación de Open-Meteo: se conserva el mapa anterior");
            return 0;
        }

        // The reads stay outside the transaction; only the writes hold a connection inside it.
        List<ZoneHeat> combined =
                reportCombiner.combine(zones, reportSource.currentValidatedReports());
        transactionOperations.executeWithoutResult(
                status -> {
                    jdbcTemplate.update(DELETE_ZONES_SQL);
                    // combine() keeps the input order, so index i still matches observedAts.get(i).
                    for (int i = 0; i < combined.size(); i++) {
                        ZoneHeat zone = combined.get(i);
                        jdbcTemplate.update(
                                INSERT_ZONE_SQL,
                                zone.name(),
                                zone.latitude(),
                                zone.longitude(),
                                zone.heatScore(),
                                observedAts.get(i));
                    }
                });
        LOGGER.info("Mapa de calor generado: {} zonas guardadas", combined.size());
        return combined.size();
    }

    /** La última observación de Open-Meteo de un punto. */
    private record Observation(double heatScore, OffsetDateTime observedAt) {}
}
