package es.uma.weather.lugares;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

/** Usa un servidor HTTP local en lugar de Nominatim, para no gastar sus peticiones. */
class NominatimProveedorLugaresTest {

    private static final String USER_AGENT = "WEather-pruebas/1.0";

    private static final String RESPUESTA =
            """
            [
              {
                "place_id": 1,
                "lat": "36.7213",
                "lon": "-4.4214",
                "display_name": "Plaza de la Constitución, Centro, Málaga, España"
              },
              {
                "place_id": 2,
                "lat": "36.7196",
                "lon": "-4.4200",
                "display_name": "Calle Larios, Centro, Málaga, España"
              }
            ]
            """;

    private final AtomicReference<String> consultaRecibida = new AtomicReference<>();
    private final AtomicReference<String> userAgentRecibido = new AtomicReference<>();
    private HttpServer servidor;
    private volatile int estado = 200;
    private volatile String cuerpo = RESPUESTA;

    @BeforeEach
    void arrancarServidor() throws IOException {
        servidor = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        servidor.createContext(
                "/search",
                peticion -> {
                    consultaRecibida.set(peticion.getRequestURI().getRawQuery());
                    userAgentRecibido.set(peticion.getRequestHeaders().getFirst("User-Agent"));
                    byte[] bytes = cuerpo.getBytes(StandardCharsets.UTF_8);
                    peticion.getResponseHeaders().add("Content-Type", "application/json");
                    peticion.sendResponseHeaders(estado, bytes.length);
                    peticion.getResponseBody().write(bytes);
                    peticion.close();
                });
        servidor.start();
    }

    @AfterEach
    void pararServidor() {
        servidor.stop(0);
    }

    private NominatimProveedorLugares proveedor() {
        String url = "http://127.0.0.1:" + servidor.getAddress().getPort() + "/search";
        return new NominatimProveedorLugares(url, USER_AGENT, 5, JsonMapper.builder().build());
    }

    private Map<String, String> parametrosRecibidos() {
        return Arrays.stream(consultaRecibida.get().split("&"))
                .map(parametro -> parametro.split("=", 2))
                .collect(
                        Collectors.toMap(
                                par -> par[0],
                                par -> URLDecoder.decode(par[1], StandardCharsets.UTF_8)));
    }

    @Test
    void devuelveLosLugaresConSuNombreLatitudYLongitud() throws IOException {
        assertThat(proveedor().buscar("plaza de la constitución"))
                .containsExactly(
                        new Lugar(
                                "Plaza de la Constitución, Centro, Málaga, España",
                                36.7213,
                                -4.4214),
                        new Lugar("Calle Larios, Centro, Málaga, España", 36.7196, -4.4200));
    }

    @Test
    void preguntaIdentificandoseConSuUserAgent() throws IOException {
        proveedor().buscar("calle larios");

        assertThat(userAgentRecibido).hasValue(USER_AGENT);
    }

    @Test
    void enviaElTextoCodificadoYPideJsonConUnMaximoDeResultados() throws IOException {
        proveedor().buscar("calle larios & alameda");

        assertThat(parametrosRecibidos())
                .containsEntry("q", "calle larios & alameda")
                .containsEntry("format", "jsonv2")
                .containsEntry("limit", "5");
    }

    @Test
    void sinCoincidenciasDevuelveUnaListaVacia() throws IOException {
        cuerpo = "[]";

        assertThat(proveedor().buscar("zzzz")).isEmpty();
    }

    @Test
    void unaRespuestaQueNoEs200EsUnFallo() {
        estado = 429;

        assertThatThrownBy(() -> proveedor().buscar("calle larios"))
                .isInstanceOf(IOException.class);
    }

    @Test
    void unaRespuestaQueNoEsUnaListaDeLugaresEsUnFallo() {
        cuerpo = "{\"error\": \"Unable to geocode\"}";

        assertThatThrownBy(() -> proveedor().buscar("calle larios"))
                .isInstanceOf(IOException.class);
    }

    // El texto buscado puede revelar la ruta del usuario: no puede acabar en los logs a través
    // del mensaje de un error.
    @Test
    void elMensajeDeUnFalloNoIncluyeElTextoBuscado() {
        estado = 500;

        assertThatThrownBy(() -> proveedor().buscar("calle larios"))
                .isInstanceOf(IOException.class)
                .message()
                .doesNotContainIgnoringCase("larios");
    }
}
