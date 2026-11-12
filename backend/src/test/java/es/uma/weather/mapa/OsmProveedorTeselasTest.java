package es.uma.weather.mapa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Usa un servidor HTTP local en lugar de OpenStreetMap, para no gastar sus peticiones. */
class OsmProveedorTeselasTest {

    private static final String USER_AGENT = "WEather-pruebas/1.0";

    private final AtomicReference<String> rutaPedida = new AtomicReference<>();
    private final AtomicReference<String> userAgentRecibido = new AtomicReference<>();
    private HttpServer servidor;
    private volatile int estado = 200;

    @BeforeEach
    void arrancarServidor() throws IOException {
        servidor = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        servidor.createContext(
                "/",
                peticion -> {
                    rutaPedida.set(peticion.getRequestURI().getPath());
                    userAgentRecibido.set(peticion.getRequestHeaders().getFirst("User-Agent"));
                    byte[] cuerpo = {9, 8, 7};
                    peticion.sendResponseHeaders(estado, cuerpo.length);
                    peticion.getResponseBody().write(cuerpo);
                    peticion.close();
                });
        servidor.start();
    }

    @AfterEach
    void pararServidor() {
        servidor.stop(0);
    }

    private OsmProveedorTeselas proveedor() {
        String plantilla =
                "http://127.0.0.1:" + servidor.getAddress().getPort() + "/{z}/{x}/{y}.png";
        return new OsmProveedorTeselas(plantilla, USER_AGENT);
    }

    @Test
    void pideLaTeselaIdentificandoseConSuUserAgent() throws IOException {
        byte[] imagen = proveedor().descargar(new Tesela(13, 3995, 3196));

        assertThat(imagen).containsExactly(9, 8, 7);
        assertThat(rutaPedida).hasValue("/13/3995/3196.png");
        assertThat(userAgentRecibido).hasValue(USER_AGENT);
    }

    @Test
    void unaRespuestaQueNoEs200EsUnFallo() {
        estado = 403;

        assertThatThrownBy(() -> proveedor().descargar(new Tesela(13, 3995, 3196)))
                .isInstanceOf(IOException.class);
    }
}
