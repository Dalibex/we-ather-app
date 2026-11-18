package es.uma.weather.heatmap.service;

/**
 * Reporte de un usuario ya validado y vigente. No lleva autor, id ni hora: el algoritmo no los
 * necesita.
 *
 * @param rating sensación térmica de 1 (fresco) a 5 (mucho calor)
 */
public record ValidatedReport(double latitude, double longitude, int rating) {}
