package es.uma.weather.heatmap.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "weather.heat")
public record HeatIngestionProperties(
        String openMeteoUrl, String overpassUrl, String overpassBbox, List<GridPoint> points) {

    public HeatIngestionProperties {
        points = points == null ? List.of() : List.copyOf(points);
    }

    public record GridPoint(String name, double latitude, double longitude) {}
}
