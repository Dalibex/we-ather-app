package es.uma.weather.ingestion.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import es.uma.weather.ingestion.service.UrbanDataIngestionService;
import es.uma.weather.ingestion.service.WeatherIngestionService;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ManualIngestionControllerTest {

    @Mock private WeatherIngestionService weatherIngestionService;
    @Mock private UrbanDataIngestionService urbanDataIngestionService;
    @InjectMocks private ManualIngestionController controller;

    @Test
    void ingestWeatherReturnsSavedCountWhenSuccessful() {
        when(weatherIngestionService.ingest()).thenReturn(5);
        ResponseEntity<Map<String, Object>> response = controller.ingestWeather();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("completed", response.getBody().get("status"));
        assertEquals("Open-Meteo", response.getBody().get("source"));
        assertEquals(5, response.getBody().get("saved"));
        verify(weatherIngestionService).ingest();
        verifyNoInteractions(urbanDataIngestionService);
    }

    @Test
    void ingestWeatherReturns503WhenNothingWasSaved() {
        when(weatherIngestionService.ingest()).thenReturn(0);
        ResponseEntity<Map<String, Object>> response = controller.ingestWeather();
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("failed", response.getBody().get("status"));
        assertEquals(0, response.getBody().get("saved"));
    }

    @Test
    void ingestUrbanReturnsSavedCountWhenSuccessful() {
        when(urbanDataIngestionService.ingest()).thenReturn(3);
        ResponseEntity<Map<String, Object>> response = controller.ingestUrban();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("completed", response.getBody().get("status"));
        assertEquals("OpenStreetMap Overpass", response.getBody().get("source"));
        assertEquals(3, response.getBody().get("saved"));
        verify(urbanDataIngestionService).ingest();
        verifyNoInteractions(weatherIngestionService);
    }

    @Test
    void ingestUrbanReturns503WhenNothingWasSaved() {
        when(urbanDataIngestionService.ingest()).thenReturn(0);
        ResponseEntity<Map<String, Object>> response = controller.ingestUrban();
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("failed", response.getBody().get("status"));
        assertEquals(0, response.getBody().get("saved"));
    }
}
