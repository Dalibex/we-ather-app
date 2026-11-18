package es.uma.weather.ingestion.controller;

import es.uma.weather.ingestion.service.UrbanDataIngestionService;
import es.uma.weather.ingestion.service.WeatherIngestionService;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingestion")
public class ManualIngestionController {

    private final WeatherIngestionService weatherIngestionService;
    private final UrbanDataIngestionService urbanDataIngestionService;

    public ManualIngestionController(
            WeatherIngestionService weatherIngestionService,
            UrbanDataIngestionService urbanDataIngestionService) {
        this.weatherIngestionService = weatherIngestionService;
        this.urbanDataIngestionService = urbanDataIngestionService;
    }

    @PostMapping("/weather")
    public ResponseEntity<Map<String, Object>> ingestWeather() {
        return result(weatherIngestionService.ingest(), "Open-Meteo");
    }

    @PostMapping("/urban")
    public ResponseEntity<Map<String, Object>> ingestUrban() {
        return result(urbanDataIngestionService.ingest(), "OpenStreetMap Overpass");
    }

    private static ResponseEntity<Map<String, Object>> result(int saved, String source) {
        HttpStatus status = saved > 0 ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(status)
                .body(
                        Map.of(
                                "status", saved > 0 ? "completed" : "failed",
                                "source", source,
                                "saved", saved));
    }
}
