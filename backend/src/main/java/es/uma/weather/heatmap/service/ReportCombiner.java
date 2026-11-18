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
        // shortcut: every report counts for every zone, upgrade to nearest-zone in the next slice
        double ratingSum = reports.stream().mapToDouble(r -> (r.rating() - 1) * 25.0).sum();
        int k = properties.pesoEstimacion();
        int n = reports.size();
        return zones.stream()
                .map(
                        zone -> {
                            double adjusted = (k * zone.heatScore() + ratingSum) / (k + n);
                            return new ZoneHeat(
                                    zone.name(),
                                    zone.latitude(),
                                    zone.longitude(),
                                    Math.round(adjusted * 10.0) / 10.0);
                        })
                .toList();
    }
}
