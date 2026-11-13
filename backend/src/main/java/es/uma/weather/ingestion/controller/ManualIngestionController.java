package es.uma.weather.ingestion.controller;

import es.uma.weather.ingestion.service.UrbanDataIngestionService;
import es.uma.weather.ingestion.service.WeatherIngestionService;
import java.util.Map;
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
    public Map<String, String> ingestWeather() {
        weatherIngestionService.ingest();
        return Map.of("status", "completed", "source", "Open-Meteo");
    }

    @PostMapping("/urban")
    public Map<String, String> ingestUrban() {
        urbanDataIngestionService.ingest();
        return Map.of("status", "completed", "source", "OpenStreetMap Overpass");
    }
}
