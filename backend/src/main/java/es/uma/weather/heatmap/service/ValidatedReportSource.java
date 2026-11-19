package es.uma.weather.heatmap.service;

import java.util.List;

/** Reportes de usuarios que pueden ajustar el mapa de calor: solo los validados y vigentes. */
public interface ValidatedReportSource {

    List<ValidatedReport> currentValidatedReports();
}
