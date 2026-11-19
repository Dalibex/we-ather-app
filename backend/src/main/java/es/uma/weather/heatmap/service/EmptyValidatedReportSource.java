package es.uma.weather.heatmap.service;

import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Fuente sin reportes, mientras no se guardan (T-12.2) ni se filtran los validados (T-19.2).
 *
 * <p>La T-19.2 sustituye esta clase por la fuente real; no añade otra a su lado, porque con dos
 * implementaciones la aplicación no arranca.
 */
@Component
public class EmptyValidatedReportSource implements ValidatedReportSource {

    @Override
    public List<ValidatedReport> currentValidatedReports() {
        return List.of();
    }
}
