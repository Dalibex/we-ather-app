package es.uma.weather.shared.security;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import es.uma.weather.ingestion.controller.ManualIngestionController;
import es.uma.weather.ingestion.service.UrbanDataIngestionService;
import es.uma.weather.ingestion.service.WeatherIngestionService;
import es.uma.weather.usuarios.UsuarioService;
import es.uma.weather.usuarios.registro.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ManualIngestionController.class)
@Import(SecurityConfig.class)
class ManualIngestionSecurityTest {

    @Autowired private MockMvc mvc;

    @MockitoBean private WeatherIngestionService weatherIngestionService;

    @MockitoBean private UrbanDataIngestionService urbanDataIngestionService;

    // Dependencias utilizadas por la autenticación JWT.
    @MockitoBean private JwtService jwtService;

    @MockitoBean private UsuarioService usuarioService;

    @MockitoBean private PasswordEncoder passwordEncoder;

    @Test
    @org.springframework.security.test.context.support.WithMockUser
    void unPostAutenticadoLanzaLaIngestaMeteorologica() throws Exception {

        when(weatherIngestionService.ingest()).thenReturn(1);

        mvc.perform(post("/api/ingestion/weather"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("completed"))
                .andExpect(jsonPath("$.saved").value(1));

        verify(weatherIngestionService).ingest();
    }

    @Test
    void unPostSinAutenticarSeRechaza() throws Exception {

        mvc.perform(post("/api/ingestion/weather")).andExpect(status().isUnauthorized());

        verifyNoInteractions(weatherIngestionService);
    }
}
