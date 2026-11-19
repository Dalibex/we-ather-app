package es.uma.weather.heatmap.service;

import es.uma.weather.heatmap.config.HeatIngestionProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionOperations;

/** Genera el mapa de calor interno por zona y lo guarda en {@code zone_heat}. */
@Service
public class HeatmapGenerator {

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
        throw new UnsupportedOperationException("T-17.4: pendiente de implementar");
    }
}
