package es.uma.weather.heatmap.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @param pesoEstimacion cuántos reportes equivalentes vale la estimación meteorológica de una zona;
 *     mayor que cero
 */
@ConfigurationProperties(prefix = "weather.reportes")
public record ReportProperties(int pesoEstimacion) {

    public ReportProperties {
        if (pesoEstimacion <= 0) {
            throw new IllegalArgumentException("pesoEstimacion debe ser mayor que cero");
        }
    }
}
