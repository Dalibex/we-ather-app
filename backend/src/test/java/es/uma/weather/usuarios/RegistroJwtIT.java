package es.uma.weather.usuarios;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.uma.weather.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class, RegistroJwtIT.EndpointProtegidoTestConfig.class})
@Transactional
class RegistroJwtIT {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    private static final String CORREO = "registro-jwt@example.com";
    private static final String CONTRASENA = "ContrasenaSegura123!";

    @Test
    void registraUsuarioIniciaSesionYAccedeConElJwt() throws Exception {

        // 1. Registro: no necesita JWT.
        mockMvc.perform(
                        post("/usuarios/registro")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                {
                                    "correo": "%s",
                                    "contrasena": "%s",
                                    "aceptaPrivacidad": true
                                }
                                """
                                                .formatted(CORREO, CONTRASENA)))
                .andExpect(status().isCreated());

        // 2. Login: obtiene el JWT.
        String respuestaLogin =
                mockMvc.perform(
                                post("/usuarios/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                """
                                        {
                                            "correo": "%s",
                                            "contrasena": "%s"
                                        }
                                        """
                                                        .formatted(CORREO, CONTRASENA)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.token").isNotEmpty())
                        .andExpect(jsonPath("$.tipo").value("Bearer"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        JsonNode json = objectMapper.readTree(respuestaLogin);
        String token = json.get("token").asText();

        // 3. Acceso al endpoint protegido usando el JWT.
        mockMvc.perform(get("/api/test/protegido").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("autenticado"));
    }

    @Test
    void noPermiteAccederAlEndpointProtegidoSinJwt() throws Exception {

        mockMvc.perform(get("/api/test/protegido")).andExpect(status().isUnauthorized());
    }

    @Test
    void noPermiteAccederConUnJwtManipulado() throws Exception {

        mockMvc.perform(
                        get("/api/test/protegido")
                                .header("Authorization", "Bearer token.invalido.manipulado"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void elRegistroNoRequiereAutenticacionPrevia() throws Exception {

        mockMvc.perform(
                        post("/usuarios/registro")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                {
                                    "correo": "sin-token@example.com",
                                    "contrasena": "ContrasenaSegura123!",
                                    "aceptaPrivacidad": true
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void noPermiteIniciarSesionConUnaContrasenaIncorrecta() throws Exception {

        // Primero se registra el usuario.
        mockMvc.perform(
                        post("/usuarios/registro")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                {
                                    "correo": "password-incorrecto@example.com",
                                    "contrasena": "ContrasenaSegura123!",
                                    "aceptaPrivacidad": true
                                }
                                """))
                .andExpect(status().isCreated());

        // Después se intenta iniciar sesión con otra contraseña.
        mockMvc.perform(
                        post("/usuarios/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                {
                                    "correo": "password-incorrecto@example.com",
                                    "contrasena": "OtraContrasena123!"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    /** Endpoint temporal de pruebas. Está protegido por la regla anyRequest().authenticated(). */
    @TestConfiguration(proxyBeanMethods = false)
    static class EndpointProtegidoTestConfig {

        @Bean
        EndpointProtegido endpointProtegido() {
            return new EndpointProtegido();
        }
    }

    @RestController
    static class EndpointProtegido {

        @GetMapping("/api/test/protegido")
        String protegido() {
            return "autenticado";
        }
    }
}
