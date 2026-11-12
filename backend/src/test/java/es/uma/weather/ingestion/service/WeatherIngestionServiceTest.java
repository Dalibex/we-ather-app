package es.uma.weather.ingestion.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import es.uma.weather.heatmap.config.HeatIngestionProperties;
import es.uma.weather.heatmap.config.HeatIngestionProperties.GridPoint;
import es.uma.weather.heatmap.service.HeatScoreCalculator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;

class WeatherIngestionServiceTest {

    private JdbcTemplate jdbcTemplate;
    private MockRestServiceServer server;
    private WeatherIngestionService service;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class);
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        HeatIngestionProperties properties =
                new HeatIngestionProperties(
                        "https://api.open-meteo.test/v1/forecast",
                        "https://overpass.test/api/interpreter",
                        "36.6,-4.6,36.8,-4.3",
                        List.of(new GridPoint("Centro", 36.72, -4.42)));
        service =
                new WeatherIngestionService(
                        builder,
                        JsonMapper.builder().build(),
                        jdbcTemplate,
                        properties,
                        new HeatScoreCalculator());
    }

    @Test
    void descargaDatosMeteorologicosYGuardaLaObservacion() {
        server.expect(requestTo(org.hamcrest.Matchers.containsString("latitude=36.72")))
                .andRespond(
                        withSuccess(
                                """
                                {
                                  "utc_offset_seconds": 3600,
                                  "current": {
                                    "time": "2026-07-20T12:00",
                                    "temperature_2m": 34.0,
                                    "relative_humidity_2m": 45,
                                    "wind_speed_10m": 8.0
                                  },
                                  "hourly": {
                                    "time": ["2026-07-20T11:00", "2026-07-20T12:00"],
                                    "shortwave_radiation": [500, 700],
                                    "uv_index": [5, 7]
                                  }
                                }
                                """,
                                org.springframework.http.MediaType.APPLICATION_JSON));

        service.ingest();

        server.verify();
        verify(jdbcTemplate)
                .update(
                        anyString(),
                        eq("OPEN_METEO"),
                        eq("Centro"),
                        eq(36.72),
                        eq(-4.42),
                        any(),
                        eq(34.0),
                        eq(45.0),
                        eq(8.0),
                        eq(700.0),
                        eq(7.0),
                        any(),
                        any(),
                        eq(-4.42),
                        eq(36.72));
    }

    @Test
    void ignoraUnaRespuestaSinDatosCurrentValidos() {
        server.expect(requestTo(org.hamcrest.Matchers.containsString("latitude=36.72")))
                .andRespond(
                        withSuccess(
                                "{\"current\":{},\"hourly\":{}}",
                                org.springframework.http.MediaType.APPLICATION_JSON));

        service.ingest();

        server.verify();
        verify(jdbcTemplate, never()).update(anyString(), any(Object[].class));
    }
}
