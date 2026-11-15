package es.uma.weather.mapa;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class TeselasControllerTest {

    @TempDir Path directorio;

    private MockMvc mvc(ProveedorTeselas proveedor) {
        Clock reloj = Clock.systemUTC();
        ServicioTeselas servicio =
                new ServicioTeselas(
                        proveedor, new CacheTeselas(directorio, reloj), Duration.ofDays(7), reloj);
        return MockMvcBuilders.standaloneSetup(new TeselasController(servicio)).build();
    }

    @Test
    void devuelveLaImagenDeUnaTeselaDeMalaga() throws Exception {
        mvc(tesela -> new byte[] {1, 2, 3})
                .perform(get("/api/v1/teselas/13/3995/3196.png"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(new byte[] {1, 2, 3}))
                .andExpect(header().string("Cache-Control", "max-age=604800, public"));
    }

    @Test
    void unaTeselaFueraDeMalagaNoExiste() throws Exception {
        mvc(tesela -> new byte[] {1})
                .perform(get("/api/v1/teselas/13/4011/3088.png"))
                .andExpect(status().isNotFound());
    }

    @Test
    void unasCoordenadasImposiblesNoExisten() throws Exception {
        mvc(tesela -> new byte[] {1})
                .perform(get("/api/v1/teselas/25/0/0.png"))
                .andExpect(status().isNotFound());
    }

    @Test
    void siNoHayTeselaQueServirElServicioNoEstaDisponible() throws Exception {
        mvc(tesela -> {
                    throw new IOException("OSM no responde");
                })
                .perform(get("/api/v1/teselas/13/3995/3196.png"))
                .andExpect(status().isServiceUnavailable());
    }
}
