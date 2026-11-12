package es.uma.weather.ingestion.service;

import static org.assertj.core.api.Assertions.assertThat;

import es.uma.weather.TestcontainersConfiguration;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;

/** Comprueba ambas ingestas contra PostgreSQL/PostGIS real y HTTP simulado. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Import({TestcontainersConfiguration.class, IngestionPersistenceIT.MockHttpConfiguration.class})
class IngestionPersistenceIT {

    private static final MockWebServer MOCK_WEB_SERVER = startMockWebServer();

    @Autowired private WeatherIngestionService weatherIngestionService;
    @Autowired private UrbanDataIngestionService urbanDataIngestionService;
    @Autowired private JdbcTemplate jdbcTemplate;

    private static MockWebServer startMockWebServer() {
        try {
            MockWebServer server = new MockWebServer();
            server.start();
            return server;
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "No se pudo iniciar el servidor HTTP de pruebas", exception);
        }
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "weather.heat.open-meteo-url",
                () -> MOCK_WEB_SERVER.url("/v1/forecast").toString());
        registry.add(
                "weather.heat.overpass-url",
                () -> MOCK_WEB_SERVER.url("/api/interpreter").toString());
        registry.add("weather.heat.overpass-bbox", () -> "36.6,-4.6,36.8,-4.3");
        registry.add("weather.heat.points[0].name", () -> "Integration point");
        registry.add("weather.heat.points[0].latitude", () -> "36.72");
        registry.add("weather.heat.points[0].longitude", () -> "-4.42");
    }

    @AfterAll
    static void stopMockWebServer() throws IOException {
        MOCK_WEB_SERVER.shutdown();
    }

    @BeforeEach
    void cleanDatabaseAndConfigureHttpResponses() {
        jdbcTemplate.update("DELETE FROM weather_observations");
        jdbcTemplate.update("DELETE FROM street_segments");

        MOCK_WEB_SERVER.setDispatcher(
                new Dispatcher() {
                    @Override
                    public MockResponse dispatch(RecordedRequest request) {
                        String path = request.getPath();

                        if ("/v1/forecast".equals(path)) {
                            return new MockResponse()
                                    .setResponseCode(200)
                                    .setHeader("Content-Type", "application/json")
                                    .setBody(
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
                                            """);
                        }

                        if ("/api/interpreter".equals(path)) {
                            return new MockResponse()
                                    .setResponseCode(200)
                                    .setHeader("Content-Type", "application/json")
                                    .setBody(
                                            """
                                            {
                                              "elements": [
                                                {
                                                  "type": "way",
                                                  "id": 987654,
                                                  "tags": {
                                                    "highway": "residential",
                                                    "name": "Calle de prueba"
                                                  },
                                                  "geometry": [
                                                    {"lat": 36.72, "lon": -4.42},
                                                    {"lat": 36.721, "lon": -4.421}
                                                  ]
                                                }
                                              ]
                                            }
                                            """);
                        }

                        return new MockResponse().setResponseCode(404);
                    }
                });
    }

    @Test
    void ingestaMeteorologicaPersisteDatosYGeografiaEnPostgis() throws InterruptedException {
        weatherIngestionService.ingest();

        RecordedRequest request = MOCK_WEB_SERVER.takeRequest(1, TimeUnit.SECONDS);
        assertThat(request).isNotNull();
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).startsWith("/v1/forecast");

        var observation =
                jdbcTemplate.queryForMap(
                        """
                        SELECT source, grid_name, temperature_c, relative_humidity_pct,
                               shortwave_radiation_wm2, uv_index, heat_score,
                               ST_X(location::geometry) AS longitude,
                               ST_Y(location::geometry) AS latitude
                        FROM weather_observations
                        """);

        assertThat(observation)
                .containsEntry("source", "OPEN_METEO")
                .containsEntry("grid_name", "Integration point");
        assertThat(((Number) observation.get("temperature_c")).doubleValue()).isEqualTo(34.0);
        assertThat(((Number) observation.get("shortwave_radiation_wm2")).doubleValue())
                .isEqualTo(700.0);
        assertThat(((Number) observation.get("uv_index")).doubleValue()).isEqualTo(7.0);
        assertThat(((Number) observation.get("longitude")).doubleValue()).isEqualTo(-4.42);
        assertThat(((Number) observation.get("latitude")).doubleValue()).isEqualTo(36.72);
        assertThat(((Number) observation.get("heat_score")).doubleValue()).isBetween(0.0, 100.0);
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class MockHttpConfiguration {

        @Bean
        @Primary
        RestClient.Builder testRestClientBuilder() {
            return RestClient.builder();
        }
    }

    @Test
    void ingestaUrbanaPersisteLaGeometriaLineStringEnPostgis() throws InterruptedException {
        urbanDataIngestionService.ingest();

        RecordedRequest request = MOCK_WEB_SERVER.takeRequest(1, TimeUnit.SECONDS);
        assertThat(request).isNotNull();
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).isEqualTo("/api/interpreter");

        var segment =
                jdbcTemplate.queryForMap(
                        "SELECT osm_way_id, highway, name, ST_AsText(geom) AS wkt FROM street_segments");

        assertThat(segment)
                .containsEntry("osm_way_id", 987654L)
                .containsEntry("highway", "residential")
                .containsEntry("name", "Calle de prueba")
                .containsEntry("wkt", "LINESTRING(-4.42 36.72,-4.421 36.721)");
    }
}
