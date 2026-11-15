package es.uma.weather.shared.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

/** El health check y las teselas del mapa son públicos; el resto de rutas pide autenticación. */
@SpringBootTest
@AutoConfigureMockMvc
@Import(es.uma.weather.TestcontainersConfiguration.class)
class SecurityConfigIT {

    @Autowired private MockMvc mvc;

    @Test
    void elHealthCheckEsPublico() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }

    // Una tesela de fuera de Málaga: se rechaza con 404 sin llegar a pedirla a OpenStreetMap.
    @Test
    void lasTeselasDelMapaSonPublicas() throws Exception {
        mvc.perform(get("/api/v1/teselas/13/4011/3088.png")).andExpect(status().isNotFound());
    }

    @Test
    void elRestoDeRutasPideAutenticacion() throws Exception {
        mvc.perform(get("/api/v1/rutas")).andExpect(status().isUnauthorized());
    }
}
