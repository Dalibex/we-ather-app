package es.uma.weather.heatmap.scheduling;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import es.uma.weather.heatmap.service.HeatmapGenerator;
import es.uma.weather.ingestion.service.UrbanDataIngestionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("HeatmapScheduler: qué hace cada tarea periódica cuando se dispara")
class HeatmapSchedulerTest {

    private static final String URBAN_CRON_ENABLED = "0 0 4 * * MON";
    private static final String URBAN_CRON_DISABLED = "-";

    @Mock private HeatmapGenerator heatmapGenerator;
    @Mock private UrbanDataIngestionService urbanDataIngestionService;

    private HeatmapScheduler schedulerWithUrbanCron(String urbanCron) {
        return new HeatmapScheduler(heatmapGenerator, urbanDataIngestionService, urbanCron);
    }

    @Test
    @DisplayName("Mock: generateHeatmap regenera el mapa de calor una vez")
    void generateHeatmap_whenTriggered_callsGeneratorOnce() {
        // Arrange
        HeatmapScheduler scheduler = schedulerWithUrbanCron(URBAN_CRON_ENABLED);

        // Act
        scheduler.generateHeatmap();

        // Assert
        verify(heatmapGenerator).generate();
    }

    @Test
    @DisplayName("Mock: refreshUrbanData vuelve a cargar las calles una vez")
    void refreshUrbanData_whenTriggered_callsIngestOnce() {
        // Arrange
        HeatmapScheduler scheduler = schedulerWithUrbanCron(URBAN_CRON_ENABLED);

        // Act
        scheduler.refreshUrbanData();

        // Assert
        verify(urbanDataIngestionService).ingest();
    }

    @Nested
    @DisplayName("refreshUrbanDataIfOutdated (al arrancar)")
    class RefreshUrbanDataIfOutdated {

        @Test
        @DisplayName("Mock: con la carga periódica activa y las calles desactualizadas, las carga")
        void refreshUrbanDataIfOutdated_cronEnabledAndOutdated_callsIngestOnce() {
            // Arrange
            when(urbanDataIngestionService.isOutdated()).thenReturn(true);
            HeatmapScheduler scheduler = schedulerWithUrbanCron(URBAN_CRON_ENABLED);

            // Act
            scheduler.refreshUrbanDataIfOutdated();

            // Assert
            verify(urbanDataIngestionService).ingest();
        }

        @Test
        @DisplayName("Mock: con la carga periódica activa y las calles al día, no las carga")
        void refreshUrbanDataIfOutdated_cronEnabledAndUpToDate_doesNotCallIngest() {
            // Arrange: sin stub, el mock devuelve false en isOutdated()
            HeatmapScheduler scheduler = schedulerWithUrbanCron(URBAN_CRON_ENABLED);

            // Act
            scheduler.refreshUrbanDataIfOutdated();

            // Assert
            verify(urbanDataIngestionService).isOutdated();
            verifyNoMoreInteractions(urbanDataIngestionService);
        }

        @Test
        @DisplayName("Mock: con la carga periódica desactivada ni consulta ni carga las calles")
        void refreshUrbanDataIfOutdated_cronDisabled_doesNotConsultNorIngest() {
            // Arrange
            HeatmapScheduler scheduler = schedulerWithUrbanCron(URBAN_CRON_DISABLED);

            // Act
            scheduler.refreshUrbanDataIfOutdated();

            // Assert
            verifyNoInteractions(urbanDataIngestionService);
        }
    }
}
