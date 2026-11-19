package es.uma.weather.heatmap.config;

import java.util.HashSet;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "weather.heat")
public record HeatIngestionProperties(
        String openMeteoUrl,
        String overpassUrl,
        String overpassBbox,
        String userAgent,
        List<GridPoint> points) {

    public HeatIngestionProperties {
        points = points == null ? List.of() : List.copyOf(points);
        // zone_heat usa el nombre como clave primaria: dos puntos iguales se pisarían.
        var names = new HashSet<String>();
        for (GridPoint point : points) {
            if (!names.add(point.name())) {
                throw new IllegalArgumentException("Nombre de punto repetido: " + point.name());
            }
        }
    }

    public record GridPoint(String name, double latitude, double longitude) {}
}
