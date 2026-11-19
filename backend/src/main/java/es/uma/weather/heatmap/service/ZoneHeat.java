package es.uma.weather.heatmap.service;

/** Índice de exposición al calor (0-100) de una zona del mapa de calor interno. */
public record ZoneHeat(String name, double latitude, double longitude, double heatScore) {}
