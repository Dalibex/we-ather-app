package es.uma.weather.heatmap.dto;

import java.time.Instant;

public record HeatCellDto(
        String name,
        double latitude,
        double longitude,
        double temperatureC,
        double relativeHumidityPct,
        double windSpeedKmh,
        double radiationWm2,
        double uvIndex,
        double heatScore,
        String heatLevel,
        Instant observedAt) {}
