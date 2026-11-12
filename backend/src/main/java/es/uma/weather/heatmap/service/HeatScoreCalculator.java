package es.uma.weather.heatmap.service;

import org.springframework.stereotype.Component;

/**
 * Calcula un índice heurístico de exposición térmica (0-100). No es una medición fisiológica ni
 * sustituye a un índice UTCI/WBGT validado.
 */
@Component
public class HeatScoreCalculator {

    public double calculate(
            double temperatureC,
            double relativeHumidity,
            double windSpeedKmh,
            double shortwaveRadiationWm2) {
        double temperature = normalize(temperatureC, 20.0, 42.0);
        double humidity = normalize(relativeHumidity, 20.0, 100.0);
        double solar = normalize(shortwaveRadiationWm2, 0.0, 900.0);
        double lowWind = 1.0 - normalize(windSpeedKmh, 0.0, 30.0);

        double score =
                100.0 * (0.40 * temperature + 0.20 * humidity + 0.25 * solar + 0.15 * lowWind);
        return Math.round(Math.max(0.0, Math.min(100.0, score)) * 10.0) / 10.0;
    }

    public String level(double score) {
        if (score < 25.0) return "LOW";
        if (score < 50.0) return "MODERATE";
        if (score < 75.0) return "HIGH";
        return "VERY_HIGH";
    }

    private double normalize(double value, double min, double max) {
        return Math.max(0.0, Math.min(1.0, (value - min) / (max - min)));
    }
}
