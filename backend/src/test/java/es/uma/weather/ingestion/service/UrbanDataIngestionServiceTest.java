package es.uma.weather.ingestion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import es.uma.weather.heatmap.config.HeatIngestionProperties;
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
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;

class UrbanDataIngestionServiceTest {

    private JdbcTemplate jdbcTemplate;
    private MockRestServiceServer server;
    private UrbanDataIngestionService service;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class);
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        HeatIngestionProperties properties =
                new HeatIngestionProperties(
                        "https://open-meteo.test",
                        "https://overpass.test/api/interpreter",
                        "36.6,-4.6,36.8,-4.3",
                        List.of());
        service =
                new UrbanDataIngestionService(
                        builder,
                        JsonMapper.builder().build(),
                        jdbcTemplate,
                        TransactionOperations.withoutTransaction(),
                        properties,
                        new LimitadorPeticiones(
                                Map.of("overpass", new Limite(100, Duration.ofDays(1))),
                                Clock.systemUTC()));
    }

    @Test
    void transformaLaGeometriaDeOverpassEnUnTramoPostgis() {
        server.expect(requestTo("https://overpass.test/api/interpreter"))
                .andRespond(
                        withSuccess(
                                """
                                {
                                  "elements": [
                                    {
                                      "type": "way",
                                      "id": 12345,
                                      "tags": {"highway": "residential", "name": "Calle Test"},
                                      "geometry": [
                                        {"lat": 36.72, "lon": -4.42},
                                        {"lat": 36.721, "lon": -4.421}
                                      ]
                                    }
                                  ]
                                }
                                """,
                                org.springframework.http.MediaType.APPLICATION_JSON));

        service.ingest();

        server.verify();
        verify(jdbcTemplate)
                .update(
                        anyString(),
                        eq(12345L),
                        eq("residential"),
                        eq("Calle Test"),
                        eq("LINESTRING(-4.42 36.72,-4.421 36.721)"));
    }

    @Test
    void omiteGeometriasConCoordenadasFueraDeRango() {
        server.expect(requestTo("https://overpass.test/api/interpreter"))
                .andRespond(
                        withSuccess(
                                """
                                {"elements":[
                                  {"type":"way","id":12346,
                                   "tags":{"highway":"footway"},
                                   "geometry":[{"lat":95,"lon":-4.42},{"lat":36.72,"lon":-4.421}]}
                                ]}
                                """,
                                org.springframework.http.MediaType.APPLICATION_JSON));

        service.ingest();

        server.verify();
        verify(jdbcTemplate, never()).update(anyString(), any(Object[].class));
    }

    @Test
    void noGuardaNadaSiOverpassDevuelveUnaRespuestaParcial() {
        // Si la consulta se queda sin tiempo, Overpass responde 200 con «remark» y solo parte de
        // las vías.
        server.expect(requestTo("https://overpass.test/api/interpreter"))
                .andRespond(
                        withSuccess(
                                """
                                {"remark":"runtime error: Query timed out after 61 seconds.",
                                 "elements":[
                                  {"type":"way","id":12347,
                                   "tags":{"highway":"residential"},
                                   "geometry":[{"lat":36.72,"lon":-4.42},{"lat":36.721,"lon":-4.421}]}
                                ]}
                                """,
                                org.springframework.http.MediaType.APPLICATION_JSON));

        assertEquals(0, service.ingest());

        server.verify();
        verifyNoInteractions(jdbcTemplate);
    }

    @Test
    void noTocaLasCallesSiOverpassNoDevuelveNingunaVia() {
        // Una respuesta sin vías nunca es Málaga: retirar las que faltan las borraría todas.
        server.expect(requestTo("https://overpass.test/api/interpreter"))
                .andRespond(
                        withSuccess(
                                "{\"elements\":[]}",
                                org.springframework.http.MediaType.APPLICATION_JSON));

        assertEquals(0, service.ingest());

        server.verify();
        verifyNoInteractions(jdbcTemplate);
    }

    @Test
    void respetaElLimiteDePeticionesDeOverpass() {
        RestClient.Builder limitedBuilder = RestClient.builder();
        MockRestServiceServer limitedServer = MockRestServiceServer.bindTo(limitedBuilder).build();
        HeatIngestionProperties limitedProperties =
                new HeatIngestionProperties(
                        "https://open-meteo.test",
                        "https://overpass.test/api/interpreter",
                        "36.6,-4.6,36.8,-4.3",
                        List.of());
        UrbanDataIngestionService limitedService =
                new UrbanDataIngestionService(
                        limitedBuilder,
                        JsonMapper.builder().build(),
                        jdbcTemplate,
                        TransactionOperations.withoutTransaction(),
                        limitedProperties,
                        new LimitadorPeticiones(
                                Map.of("overpass", new Limite(1, Duration.ofDays(1))),
                                Clock.systemUTC()));
        limitedServer
                .expect(requestTo("https://overpass.test/api/interpreter"))
                .andRespond(
                        withSuccess(
                                "{\"elements\":[]}",
                                org.springframework.http.MediaType.APPLICATION_JSON));
        limitedService.ingest();
        org.junit.jupiter.api.Assertions.assertEquals(0, limitedService.ingest());
        limitedServer.verify();
    }
}
