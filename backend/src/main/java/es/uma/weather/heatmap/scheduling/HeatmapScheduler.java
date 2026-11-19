package es.uma.weather.heatmap.scheduling;

import es.uma.weather.heatmap.service.HeatmapGenerator;
import es.uma.weather.ingestion.service.UrbanDataIngestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Ejecuta de forma periódica la generación del mapa de calor y la carga de las calles. */
@Component
public class HeatmapScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeatmapScheduler.class);

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
    @Scheduled(cron = "${weather.heat.schedule.generation}", zone = "Europe/Madrid")
    public void generateHeatmap() {
        heatmapGenerator.generate();
    }

    /** Vuelve a cargar las calles de Overpass. */
    @Scheduled(cron = "${weather.heat.schedule.urban}", zone = "Europe/Madrid")
    public void refreshUrbanData() {
        urbanDataIngestionService.ingest();
    }

    /**
     * Al arrancar, carga las calles si faltan o están desactualizadas: un cron no recupera las
     * ejecuciones que se perdieron con el servidor apagado. No hace nada si la carga periódica está
     * desactivada ({@code -}).
     */
    @EventListener(ApplicationReadyEvent.class)
    public void refreshUrbanDataIfOutdated() {
        if (Scheduled.CRON_DISABLED.equals(urbanCron)) {
            return;
        }
        if (urbanDataIngestionService.isOutdated()) {
            LOGGER.info("Calles ausentes o desactualizadas: se cargan al arrancar");
            urbanDataIngestionService.ingest();
        }
    }
}
