package es.uma.weather.heatmap.service;

import es.uma.weather.heatmap.config.ReportProperties;
import java.util.List;

/** Ajusta el calor estimado de cada zona con los reportes validados y vigentes de sus usuarios. */
public class ReportCombiner {

    private final ReportProperties properties;

    public ReportCombiner(ReportProperties properties) {
        this.properties = properties;
    }

    public List<ZoneHeat> combine(List<ZoneHeat> zones, List<ValidatedReport> reports) {
        throw new UnsupportedOperationException("T-17.3: pendiente de implementar");
    }
}
