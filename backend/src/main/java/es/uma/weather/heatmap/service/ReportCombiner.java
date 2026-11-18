package es.uma.weather.heatmap.service;

import es.uma.weather.heatmap.config.ReportProperties;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/** Ajusta el calor estimado de cada zona con los reportes validados y vigentes de sus usuarios. */
@Component
public class ReportCombiner {

    private final ReportProperties properties;

    public ReportCombiner(ReportProperties properties) {
        this.properties = properties;
    }

    public List<ZoneHeat> combine(List<ZoneHeat> zones, List<ValidatedReport> reports) {
        int k = properties.pesoEstimacion();
        double[] ratingSums = new double[zones.size()];
        int[] counts = new int[zones.size()];
        for (ValidatedReport report : reports) {
            int nearest = nearestZone(zones, report);
            if (nearest >= 0) {
                ratingSums[nearest] += (report.rating() - 1) * 25.0;
                counts[nearest]++;
            }
        }
        List<ZoneHeat> result = new ArrayList<>();
        for (int i = 0; i < zones.size(); i++) {
            ZoneHeat zone = zones.get(i);
            double adjusted = (k * zone.heatScore() + ratingSums[i]) / (k + counts[i]);
            result.add(
                    new ZoneHeat(
                            zone.name(),
                            zone.latitude(),
                            zone.longitude(),
                            Math.round(adjusted * 10.0) / 10.0));
        }
        return result;
    }

    /** Index of the closest zone (first one wins ties), or -1 if there are none. */
    private static int nearestZone(List<ZoneHeat> zones, ValidatedReport report) {
        // Equirectangular approximation: accurate enough to rank points at city scale.
        double cosLat = Math.cos(Math.toRadians(report.latitude()));
        int nearest = -1;
        double best = Double.MAX_VALUE;
        for (int i = 0; i < zones.size(); i++) {
            double dLat = zones.get(i).latitude() - report.latitude();
            double dLon = (zones.get(i).longitude() - report.longitude()) * cosLat;
            double distance = dLat * dLat + dLon * dLon;
            if (distance < best) {
                best = distance;
                nearest = i;
            }
        }
        return nearest;
    }
}
