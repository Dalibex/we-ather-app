package es.uma.weather.ingestion.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import es.uma.weather.heatmap.config.HeatIngestionProperties;
import es.uma.weather.heatmap.config.HeatIngestionProperties.GridPoint;
import es.uma.weather.heatmap.service.HeatScoreCalculator;
import es.uma.weather.shared.proveedores.LimitadorPeticiones;
import es.uma.weather.shared.proveedores.Limite;
import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.Map;
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
                        "WEather/test",
                        List.of(new GridPoint("Centro", 36.72, -4.42)));
        service =
                new WeatherIngestionService(
                        builder,
                        JsonMapper.builder().build(),
                        jdbcTemplate,
                        properties,
                        new HeatScoreCalculator(),
                        new LimitadorPeticiones(
                                Map.of("open-meteo", new Limite(100, Duration.ofHours(1))),
                                Clock.systemUTC()));
    }

    @Test
    void descargaDatosMeteorologicosYGuardaLaObservacion() {
        server.expect(requestTo(org.hamcrest.Matchers.containsString("latitude=36.72")))
                .andExpect(requestTo(org.hamcrest.Matchers.containsString("cloud_cover")))
                .andExpect(header("User-Agent", "WEather/test"))
                .andRespond(
                        withSuccess(
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
                                    "time": ["2026-07-20T11:00", "2026-07-20T12:00", "2026-07-20T13:00"],
                                    "shortwave_radiation": [500, 700, 900],
                                    "uv_index": [5, 7, 9]
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
                        eq(20.0),
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

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.CsvSource({
        "temperature_2m,34.0",
        "relative_humidity_2m,45",
        "wind_speed_10m,8.0",
        "cloud_cover,20",
        "shortwave_radiation,700",
        "uv_index,7"
    })
    void noGuardaSiFaltaUnCampoMeteorologico(String field, String value) {
        assertIncompleteFieldIsRejected(field, value, false);
    }

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.CsvSource({
        "temperature_2m,34.0",
        "relative_humidity_2m,45",
        "wind_speed_10m,8.0",
        "cloud_cover,20",
        "shortwave_radiation,700",
        "uv_index,7"
    })
    void noGuardaSiUnCampoMeteorologicoEsNull(String field, String value) {
        assertIncompleteFieldIsRejected(field, value, true);
    }

    private void assertIncompleteFieldIsRejected(String field, String value, boolean nullValue) {
        String valid =
                """
                {"utc_offset_seconds":3600,
                 "current":{"time":"2026-07-20T12:00","temperature_2m":34.0,
                 "relative_humidity_2m":45,"wind_speed_10m":8.0,"cloud_cover":20},
                 "hourly":{"time":["2026-07-20T11:00","2026-07-20T12:00","2026-07-20T13:00"],
                 "shortwave_radiation":[500,700,900],"uv_index":[5,7,9]}}
                """;
        String target;
        String replacement;
        if ("shortwave_radiation".equals(field) || "uv_index".equals(field)) {
            String values = "shortwave_radiation".equals(field) ? "[500,700,900]" : "[5,7,9]";
            target = "\"" + field + "\":" + values;
            if (nullValue) {
                String nullValues = values.replace("," + value + ",", ",null,");
                replacement = "\"" + field + "\":" + nullValues;
            } else {
                replacement = "";
            }
        } else {
            target = "\"" + field + "\":" + value;
            replacement = nullValue ? "\"" + field + "\":null" : "";
        }
        String invalid = valid.replace(target, replacement);
        org.junit.jupiter.api.Assertions.assertNotEquals(valid, invalid);
        server.expect(requestTo(org.hamcrest.Matchers.containsString("latitude=36.72")))
                .andRespond(
                        withSuccess(invalid, org.springframework.http.MediaType.APPLICATION_JSON));

        service.ingest();

        server.verify();
        verify(jdbcTemplate, never()).update(anyString(), any(Object[].class));
    }

    @Test
    void noGuardaSiFaltaElDesfaseUtc() {
        server.expect(requestTo(org.hamcrest.Matchers.containsString("latitude=36.72")))
                .andRespond(
                        withSuccess(
                                """
                        {"current":{"time":"2026-07-20T12:00","temperature_2m":34.0,
                         "relative_humidity_2m":45,"wind_speed_10m":8.0},
                         "hourly":{"time":["2026-07-20T12:00"],"shortwave_radiation":[700],"uv_index":[7]}}
                        """,
                                org.springframework.http.MediaType.APPLICATION_JSON));
        service.ingest();
        server.verify();
        verify(jdbcTemplate, never()).update(anyString(), any(Object[].class));
    }

    @Test
    void noGuardaSiFaltaLaHoraActual() {
        server.expect(requestTo(org.hamcrest.Matchers.containsString("latitude=36.72")))
                .andRespond(
                        withSuccess(
                                """
                        {"utc_offset_seconds":3600,
                         "current":{"temperature_2m":34.0,"relative_humidity_2m":45,"wind_speed_10m":8.0},
                         "hourly":{"time":["2026-07-20T12:00"],"shortwave_radiation":[700],"uv_index":[7]}}
                        """,
                                org.springframework.http.MediaType.APPLICATION_JSON));
        service.ingest();
        server.verify();
        verify(jdbcTemplate, never()).update(anyString(), any(Object[].class));
    }

    @Test
    void respetaElLimiteDePeticionesDelProveedor() {
        RestClient.Builder limitedBuilder = RestClient.builder();
        MockRestServiceServer limitedServer = MockRestServiceServer.bindTo(limitedBuilder).build();
        HeatIngestionProperties limitedProperties =
                new HeatIngestionProperties(
                        "https://api.open-meteo.test/v1/forecast",
                        "https://overpass.test/api/interpreter",
                        "36.6,-4.6,36.8,-4.3",
                        "WEather/test",
                        List.of(new GridPoint("Centro", 36.72, -4.42)));
        WeatherIngestionService limitedService =
                new WeatherIngestionService(
                        limitedBuilder,
                        JsonMapper.builder().build(),
                        jdbcTemplate,
                        limitedProperties,
                        new HeatScoreCalculator(),
                        new LimitadorPeticiones(
                                Map.of("open-meteo", new Limite(1, Duration.ofHours(1))),
                                Clock.systemUTC()));
        limitedServer
                .expect(requestTo(org.hamcrest.Matchers.containsString("latitude=36.72")))
                .andRespond(
                        withSuccess(
                                """
                        {"utc_offset_seconds":3600,"current":{"time":"2026-07-20T12:00","temperature_2m":34.0,
                        "relative_humidity_2m":45,"wind_speed_10m":8.0},
                        "hourly":{"time":["2026-07-20T12:00"],"shortwave_radiation":[700],"uv_index":[7]}}
                        """,
                                org.springframework.http.MediaType.APPLICATION_JSON));
        limitedService.ingest();
        limitedService.ingest();
        limitedServer.verify();
    }

    @Test
    void noGuardaSiElDesfaseUtcEsNull() {
        server.expect(requestTo(org.hamcrest.Matchers.containsString("latitude=36.72")))
                .andRespond(
                        withSuccess(
                                """
                        {"utc_offset_seconds":null,
                         "current":{"time":"2026-07-20T12:00","temperature_2m":34.0,
                         "relative_humidity_2m":45,"wind_speed_10m":8.0},
                         "hourly":{"time":["2026-07-20T12:00"],"shortwave_radiation":[700],"uv_index":[7]}}
                        """,
                                org.springframework.http.MediaType.APPLICATION_JSON));
        service.ingest();
        server.verify();
        verify(jdbcTemplate, never()).update(anyString(), any(Object[].class));
    }

    @Test
    void noGuardaSiLaHoraActualEsNull() {
        server.expect(requestTo(org.hamcrest.Matchers.containsString("latitude=36.72")))
                .andRespond(
                        withSuccess(
                                """
                        {"utc_offset_seconds":3600,
                         "current":{"time":null,"temperature_2m":34.0,
                         "relative_humidity_2m":45,"wind_speed_10m":8.0},
                         "hourly":{"time":["2026-07-20T12:00"],"shortwave_radiation":[700],"uv_index":[7]}}
                        """,
                                org.springframework.http.MediaType.APPLICATION_JSON));
        service.ingest();
        server.verify();
        verify(jdbcTemplate, never()).update(anyString(), any(Object[].class));
    }
}
