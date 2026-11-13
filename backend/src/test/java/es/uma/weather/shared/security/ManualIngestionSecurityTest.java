package es.uma.weather.shared.security;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import es.uma.weather.ingestion.controller.ManualIngestionController;
import es.uma.weather.ingestion.service.UrbanDataIngestionService;
import es.uma.weather.ingestion.service.WeatherIngestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** La ingesta manual se lanza con un POST autenticado, con la configuración de seguridad real. */
@WebMvcTest(ManualIngestionController.class)
@Import(SecurityConfig.class)
class ManualIngestionSecurityTest {

    @Autowired private MockMvc mvc;

    @MockitoBean private WeatherIngestionService weatherIngestionService;
    @MockitoBean private UrbanDataIngestionService urbanDataIngestionService;

    @Test
    @WithMockUser
    void unPostAutenticadoLanzaLaIngestaMeteorologica() throws Exception {
        mvc.perform(post("/api/ingestion/weather"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("completed"));

        verify(weatherIngestionService).ingest();
    }

    @Test
    void unPostSinAutenticarSeRechaza() throws Exception {
        mvc.perform(post("/api/ingestion/weather")).andExpect(status().isUnauthorized());

        verifyNoInteractions(weatherIngestionService);
    }
}
