package es.uma.weather.ingestion.service;

import static org.assertj.core.api.Assertions.assertThat;

import es.uma.weather.TestcontainersConfiguration;
import es.uma.weather.heatmap.controller.HeatmapController;
import es.uma.weather.heatmap.dto.HeatCellDto;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
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
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/** Comprueba ambas ingestas contra PostgreSQL/PostGIS real y HTTP simulado. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Import(TestcontainersConfiguration.class)
class IngestionPersistenceIT {

    private static final MockWebServer MOCK_WEB_SERVER = startMockWebServer();

    @Autowired private WeatherIngestionService weatherIngestionService;
    @Autowired private UrbanDataIngestionService urbanDataIngestionService;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private HeatmapController heatmapController;

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
        // Varias pruebas llaman a Overpass más de una vez; el límite real (2 al día) las cortaría.
        registry.add("weather.proveedores.limites.overpass.max-peticiones", () -> "100");
        // Corto, para que la prueba del proveedor que no responde no tarde.
        registry.add("spring.http.clients.read-timeout", () -> "1s");
    }

    @AfterAll
    static void stopMockWebServer() throws IOException {
        MOCK_WEB_SERVER.shutdown();
    }

    /**
     * Lo que responde el falso Overpass; cada prueba puede cambiarlo antes de llamar a la ingesta.
     */
    private volatile String overpassResponse;

    /** Si es cierto, el falso Open-Meteo tarda más que el tiempo máximo de lectura. */
    private volatile boolean openMeteoNoResponde;

    /** Si es cierto, el falso Open-Meteo responde con un error 500. */
    private volatile boolean openMeteoFalla;

    @BeforeEach
    void cleanDatabaseAndConfigureHttpResponses() throws InterruptedException {
        jdbcTemplate.update("DELETE FROM weather_observations");
        jdbcTemplate.update("DELETE FROM street_segments");
        // Descarta las peticiones que dejaron otras pruebas, para que cada una vea solo las suyas.
        while (MOCK_WEB_SERVER.takeRequest(0, TimeUnit.MILLISECONDS) != null) {
            // nada: solo se vacía la cola
        }
        overpassResponse = respuesta(via(987654L, "residential", "Calle de prueba"));
        openMeteoNoResponde = false;
        openMeteoFalla = false;

        MOCK_WEB_SERVER.setDispatcher(
                new Dispatcher() {
                    @Override
                    public MockResponse dispatch(RecordedRequest request) {
                        // getPath() includes the query string; compare only the path.
                        String path = request.getRequestUrl().encodedPath();

                        if ("/v1/forecast".equals(path) && openMeteoFalla) {
                            return new MockResponse().setResponseCode(500);
                        }

                        if ("/v1/forecast".equals(path)) {
                            return new MockResponse()
                                    .setHeadersDelay(openMeteoNoResponde ? 5 : 0, TimeUnit.SECONDS)
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
                                                "wind_speed_10m": 8.0,
                                                "cloud_cover": 20
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
                                    .setBody(overpassResponse);
                        }

                        return new MockResponse().setResponseCode(404);
                    }
                });
    }

    @Test
    void ingestaMeteorologicaPersisteDatosYGeografiaEnPostgis() throws InterruptedException {
        assertThat(weatherIngestionService.ingest()).isEqualTo(1);

        RecordedRequest request = MOCK_WEB_SERVER.takeRequest(1, TimeUnit.SECONDS);
        assertThat(request).isNotNull();
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).startsWith("/v1/forecast");

        var observation =
                jdbcTemplate.queryForMap(
                        """
                        SELECT source, grid_name, temperature_c, relative_humidity_pct,
                               cloud_cover_pct, shortwave_radiation_wm2, uv_index, heat_score,
                               ST_X(location::geometry) AS longitude,
                               ST_Y(location::geometry) AS latitude
                        FROM weather_observations
                        """);

        assertThat(observation)
                .containsEntry("source", "OPEN_METEO")
                .containsEntry("grid_name", "Integration point");
        assertThat(((Number) observation.get("temperature_c")).doubleValue()).isEqualTo(34.0);
        assertThat(((Number) observation.get("cloud_cover_pct")).doubleValue()).isEqualTo(20.0);
        assertThat(((Number) observation.get("shortwave_radiation_wm2")).doubleValue())
                .isEqualTo(700.0);
        assertThat(((Number) observation.get("uv_index")).doubleValue()).isEqualTo(7.0);
        assertThat(((Number) observation.get("longitude")).doubleValue()).isEqualTo(-4.42);
        assertThat(((Number) observation.get("latitude")).doubleValue()).isEqualTo(36.72);
        assertThat(((Number) observation.get("heat_score")).doubleValue()).isBetween(0.0, 100.0);
    }

    @Test
    void siOpenMeteoFallaElMapaDeCalorSigueConElUltimoDatoValido() {
        // HU-20: si una API falla, se usa el último dato válido guardado (T-20.1).
        assertThat(weatherIngestionService.ingest()).isEqualTo(1);
        openMeteoFalla = true;

        assertThat(weatherIngestionService.ingest()).isZero();

        List<HeatCellDto> mapa = heatmapController.getLatestHeatmap(36.68, 36.78, -4.52, -4.35);
        assertThat(mapa)
                .singleElement()
                .satisfies(
                        celda -> {
                            assertThat(celda.temperatureC()).isEqualTo(34.0);
                            assertThat(celda.observedAt())
                                    .isEqualTo(Instant.parse("2026-07-20T11:00:00Z"));
                        });
    }

    @Test
    void ingestaMeteorologicaNoSeQuedaEsperandoAUnProveedorQueNoResponde() {
        openMeteoNoResponde = true;

        long inicio = System.nanoTime();
        int guardadas = weatherIngestionService.ingest();
        Duration espera = Duration.ofNanos(System.nanoTime() - inicio);

        assertThat(guardadas).isZero();
        assertThat(espera).isLessThan(Duration.ofSeconds(4));
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT COUNT(*) FROM weather_observations", Long.class))
                .isZero();
    }

    @Test
    void ingestaUrbanaPersisteLaGeometriaLineStringEnPostgis() throws InterruptedException {
        assertThat(urbanDataIngestionService.ingest()).isEqualTo(1);

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

    @Test
    void ingestaUrbanaRetiraLasViasQueYaNoEstanEnOpenStreetMap() {
        overpassResponse =
                respuesta(
                        via(1L, "residential", "Calle que desaparece"),
                        via(2L, "residential", "Calle que sigue"));
        assertThat(urbanDataIngestionService.ingest()).isEqualTo(2);

        overpassResponse = respuesta(via(2L, "residential", "Calle que sigue"));
        assertThat(urbanDataIngestionService.ingest()).isEqualTo(1);

        assertThat(viasGuardadas()).containsExactly(2L);
    }

    @Test
    void ingestaUrbanaParcialConservaLasCallesAnteriores() {
        assertThat(urbanDataIngestionService.ingest()).isEqualTo(1);

        // Overpass responde 200 aunque la consulta se quede sin tiempo: lo indica con «remark».
        overpassResponse =
                """
                {"remark": "runtime error: Query timed out after 61 seconds.", "elements": [%s]}
                """
                        .formatted(via(2L, "residential", "Calle a medias"));
        assertThat(urbanDataIngestionService.ingest()).isZero();

        assertThat(viasGuardadas()).containsExactly(987654L);
    }

    @Test
    void ingestaUrbanaQueFallaAMitadConservaLasCallesAnteriores() {
        assertThat(urbanDataIngestionService.ingest()).isEqualTo(1);

        // «highway» admite 50 caracteres: la segunda vía falla después de actualizar la primera.
        overpassResponse =
                respuesta(
                        via(987654L, "residential", "Calle renombrada"),
                        via(2L, "x".repeat(51), "Calle con un tipo demasiado largo"));
        assertThat(urbanDataIngestionService.ingest()).isZero();

        assertThat(jdbcTemplate.queryForList("SELECT name FROM street_segments", String.class))
                .containsExactly("Calle de prueba");
    }

    /** Una vía de Overpass con su geometría, dentro de Málaga. */
    private static String via(long id, String highway, String name) {
        return """
                {"type": "way", "id": %d, "tags": {"highway": "%s", "name": "%s"},
                 "geometry": [{"lat": 36.72, "lon": -4.42}, {"lat": 36.721, "lon": -4.421}]}
                """
                .formatted(id, highway, name);
    }

    /** Respuesta completa de Overpass con las vías indicadas. */
    private static String respuesta(String... vias) {
        return "{\"elements\": [" + String.join(",", vias) + "]}";
    }

    private List<Long> viasGuardadas() {
        return jdbcTemplate.queryForList(
                "SELECT osm_way_id FROM street_segments ORDER BY osm_way_id", Long.class);
    }
}
