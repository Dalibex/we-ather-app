package es.uma.weather.heatmap.scheduling;

import es.uma.weather.heatmap.service.HeatmapGenerator;
import es.uma.weather.ingestion.service.UrbanDataIngestionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Ejecuta de forma periódica la generación del mapa de calor y la carga de las calles. */
@Component
public class HeatmapScheduler {

    private final HeatmapGenerator heatmapGenerator;
    private final UrbanDataIngestionService urbanDataIngestionService;
    private final String urbanCron;

    public HeatmapScheduler(
            HeatmapGenerator heatmapGenerator,
            UrbanDataIngestionService urbanDataIngestionService,
            @Value("${weather.heat.schedule.urban}") String urbanCron) {
        this.heatmapGenerator = heatmapGenerator;
        this.urbanDataIngestionService = urbanDataIngestionService;
        this.urbanCron = urbanCron;
    }

    /** Regenera el mapa de calor con los últimos datos guardados. */
    public void generateHeatmap() {}

    /** Vuelve a cargar las calles de Overpass. */
    public void refreshUrbanData() {}

    /**
     * Al arrancar, carga las calles si faltan o están desactualizadas: un cron no recupera las
     * ejecuciones que se perdieron con el servidor apagado. No hace nada si la carga periódica está
     * desactivada ({@code -}).
     */
    public void refreshUrbanDataIfOutdated() {}
}
