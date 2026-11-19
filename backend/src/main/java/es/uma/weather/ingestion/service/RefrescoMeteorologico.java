package es.uma.weather.ingestion.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Refresca los datos meteorológicos de Open-Meteo con la frecuencia de {@code
 * weather.heat.refresco-cron} (cada hora, HU-20).
 *
 * <p>Las peticiones de los usuarios nunca llaman a Open-Meteo: leen las observaciones guardadas. Si
 * un refresco falla en un punto, ese punto no se guarda y se sigue usando su última observación
 * válida.
 */
@Component
public class RefrescoMeteorologico {

    private final WeatherIngestionService weatherIngestionService;

    public RefrescoMeteorologico(WeatherIngestionService weatherIngestionService) {
        this.weatherIngestionService = weatherIngestionService;
    }

    @Scheduled(cron = "${weather.heat.refresco-cron}", zone = "Europe/Madrid")
    public void refrescar() {
        weatherIngestionService.ingest();
    }
}
