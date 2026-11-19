package es.uma.weather.shared.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import es.uma.weather.usuarios.registro.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

/** El health check y las teselas del mapa son públicos; el resto de rutas pide autenticación. */
@SpringBootTest
@AutoConfigureMockMvc
@Import(es.uma.weather.TestcontainersConfiguration.class)
class SecurityConfigIT {

    @Autowired private MockMvc mvc;
    @Autowired private ApplicationContext context;

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

    // Como el filtro JWT es un @Component, Spring Boot también lo registraría como filtro del
    // servidor, fuera de Spring Security. Solo debe ejecutarse dentro de la cadena de seguridad.
    @Test
    void elFiltroJwtSoloSeRegistraEnLaCadenaDeSeguridad() {
        assertThat(context.getBeansOfType(FilterRegistrationBean.class).values())
                .filteredOn(registro -> registro.getFilter() instanceof JwtAuthenticationFilter)
                .singleElement()
                .satisfies(registro -> assertThat(registro.isEnabled()).isFalse());
    }
}
