package es.uma.weather.heatmap.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(HeatIngestionProperties.class)
public class HeatmapConfig {}
