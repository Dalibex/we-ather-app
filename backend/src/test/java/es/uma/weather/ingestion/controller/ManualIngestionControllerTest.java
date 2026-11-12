package es.uma.weather.ingestion.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import es.uma.weather.ingestion.service.UrbanDataIngestionService;
import es.uma.weather.ingestion.service.WeatherIngestionService;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ManualIngestionControllerTest {

    @Mock private WeatherIngestionService weatherIngestionService;
    @Mock private UrbanDataIngestionService urbanDataIngestionService;

    @InjectMocks private ManualIngestionController controller;

    @Test
    void ingestWeatherInvokesServiceAndReturnsSource() {
        Map<String, String> response = controller.ingestWeather();

        assertEquals("completed", response.get("status"));
        assertEquals("Open-Meteo", response.get("source"));
        verify(weatherIngestionService).ingest();
        verifyNoInteractions(urbanDataIngestionService);
    }

    @Test
    void ingestUrbanInvokesServiceAndReturnsSource() {
        Map<String, String> response = controller.ingestUrban();

        assertEquals("completed", response.get("status"));
        assertEquals("OpenStreetMap Overpass", response.get("source"));
        verify(urbanDataIngestionService).ingest();
        verifyNoInteractions(weatherIngestionService);
    }
}
