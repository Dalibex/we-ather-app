package es.uma.weather.heatmap.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @param pesoEstimacion cuántos reportes equivalentes vale la estimación meteorológica de una zona
 */
@ConfigurationProperties(prefix = "weather.reportes")
public record ReportProperties(int pesoEstimacion) {}
