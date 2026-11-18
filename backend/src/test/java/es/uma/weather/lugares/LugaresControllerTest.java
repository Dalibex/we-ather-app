package es.uma.weather.lugares;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import es.uma.weather.shared.proveedores.LimitadorPeticiones;
import es.uma.weather.shared.proveedores.Limite;
import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class LugaresControllerTest {

    private static MockMvc mvc(ProveedorLugares proveedor) {
        LimitadorPeticiones limitador =
                new LimitadorPeticiones(
                        Map.of("nominatim", new Limite(1, Duration.ofSeconds(1))),
                        Clock.systemUTC());
        return MockMvcBuilders.standaloneSetup(
                        new LugaresController(new ServicioLugares(proveedor, limitador)))
                .build();
    }

    @Test
    void devuelveLosLugaresConNombreLatitudYLongitud() throws Exception {
        mvc(texto -> List.of(new Lugar("Plaza de la Constitución, Málaga", 36.7213, -4.4214)))
                .perform(get("/api/lugares").param("q", "plaza de la constitución"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(
                        content()
                                .json(
                                        """
                                        [{"nombre": "Plaza de la Constitución, Málaga",
                                          "lat": 36.7213, "lon": -4.4214}]
                                        """,
                                        JsonCompareMode.STRICT));
    }

    @Test
    void sinTextoLaPeticionNoEsValida() throws Exception {
        mvc(texto -> List.of()).perform(get("/api/lugares")).andExpect(status().isBadRequest());
    }

    @Test
    void unTextoEnBlancoNoEsValido() throws Exception {
        mvc(texto -> List.of())
                .perform(get("/api/lugares").param("q", "   "))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unTextoDemasiadoLargoNoEsValido() throws Exception {
        mvc(texto -> List.of())
                .perform(get("/api/lugares").param("q", "a".repeat(201)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void siNominatimNoRespondeLaBusquedaNoEstaDisponible() throws Exception {
        mvc(texto -> {
                    throw new IOException("Nominatim no responde");
                })
                .perform(get("/api/lugares").param("q", "calle larios"))
                .andExpect(status().isServiceUnavailable());
    }
}
