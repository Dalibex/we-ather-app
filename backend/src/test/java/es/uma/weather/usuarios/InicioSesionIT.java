package es.uma.weather.usuarios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import es.uma.weather.TestcontainersConfiguration;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

/** Inicio de sesión con correo y contraseña (HU-02, T-02.3): {@code POST /api/v1/sesiones}. */
@SpringBootTest
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class, RegistroJwtIT.EndpointProtegidoTestConfig.class})
@Transactional
class InicioSesionIT {

    private static final String CORREO = "inicio-sesion@example.com";
    private static final String CONTRASENA = "ContrasenaSegura123!";
    private static final String MENSAJE_GENERICO = "Correo o contraseña incorrectos";

    @Autowired private MockMvc mockMvc;

    @Test
    void conCredencialesCorrectasDevuelveUnTokenBearerQueDaAccesoAlContenidoProtegido()
            throws Exception {
        registrar(CORREO);

        String respuesta =
                iniciarSesion(CORREO, CONTRASENA)
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.tipo").value("Bearer"))
                        .andExpect(jsonPath("$.token").isNotEmpty())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        String token = JsonPath.read(respuesta, "$.token");

        mockMvc.perform(get("/api/test/protegido").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("autenticado"));
    }

    // Cualquiera que tenga el token puede leer su contenido (minimización, RGPD).
    @Test
    void elTokenLlevaElIdDelUsuarioYNoSuCorreo() throws Exception {
        String id = registrar(CORREO);

        String respuesta =
                iniciarSesion(CORREO, CONTRASENA).andReturn().getResponse().getContentAsString();
        String token = JsonPath.read(respuesta, "$.token");
        String contenido =
                new String(
                        Base64.getUrlDecoder().decode(token.split("\\.")[1]),
                        StandardCharsets.UTF_8);

        assertThat(contenido).contains("\"sub\":\"" + id + "\"").doesNotContain(CORREO);
    }

    @Test
    void elCorreoSeNormalizaComoEnElRegistro() throws Exception {
        registrar(CORREO);

        iniciarSesion("  Inicio-Sesion@Example.COM ", CONTRASENA).andExpect(status().isOk());
    }

    @Test
    void conUnaContrasenaIncorrectaRespondeUn401Generico() throws Exception {
        registrar(CORREO);

        iniciarSesion(CORREO, "OtraContrasena123!")
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("CREDENCIALES_INVALIDAS"))
                .andExpect(jsonPath("$.mensaje").value(MENSAJE_GENERICO));
    }

    // El mismo error que con una contraseña incorrecta: no revela qué correos están registrados.
    @Test
    void conUnCorreoNoRegistradoRespondeElMismo401Generico() throws Exception {
        iniciarSesion("no-registrado@example.com", CONTRASENA)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("CREDENCIALES_INVALIDAS"))
                .andExpect(jsonPath("$.mensaje").value(MENSAJE_GENERICO));
    }

    // BCrypt no rechaza al comprobar una contraseña de más de 72 bytes: solo cuenta los 72
    // primeros. Esta da 401 porque no coincide con la de la cuenta, no por su longitud.
    @Test
    void conUnaContrasenaDeMasDe72BytesRespondeElMismo401Generico() throws Exception {
        registrar(CORREO);

        iniciarSesion(CORREO, "a".repeat(73))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("CREDENCIALES_INVALIDAS"));
    }

    @Test
    void sinCorreoRespondeUn400() throws Exception {
        enviar("{\"contrasena\": \"%s\"}".formatted(CONTRASENA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"));
    }

    @Test
    void conUnCorreoEnBlancoRespondeUn400() throws Exception {
        iniciarSesion("   ", CONTRASENA)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"));
    }

    @Test
    void sinContrasenaRespondeUn400() throws Exception {
        enviar("{\"correo\": \"%s\"}".formatted(CORREO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"));
    }

    @Test
    void conUnaContrasenaEnBlancoRespondeUn400() throws Exception {
        iniciarSesion(CORREO, "   ")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"));
    }

    @Test
    void conUnJsonMalformadoRespondeUn400() throws Exception {
        enviar("{\"correo\": ")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"));
    }

    @Test
    void unTokenInvalidoNoImpideIniciarSesion() throws Exception {
        registrar(CORREO);

        mockMvc.perform(
                        post("/api/v1/sesiones")
                                .header("Authorization", "Bearer token.invalido.manipulado")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(credenciales(CORREO, CONTRASENA)))
                .andExpect(status().isOk());
    }

    /** Registra una cuenta y devuelve su id. */
    private String registrar(String correo) throws Exception {
        String respuesta =
                mockMvc.perform(
                                post("/api/v1/usuarios")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                """
                                                {"correo": "%s", "contrasena": "%s",
                                                 "aceptaPrivacidad": true}
                                                """
                                                        .formatted(correo, CONTRASENA)))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        return JsonPath.read(respuesta, "$.id");
    }

    private ResultActions iniciarSesion(String correo, String contrasena) throws Exception {
        return enviar(credenciales(correo, contrasena));
    }

    private static String credenciales(String correo, String contrasena) {
        return """
               {"correo": "%s", "contrasena": "%s"}
               """
                .formatted(correo, contrasena);
    }

    private ResultActions enviar(String json) throws Exception {
        return mockMvc.perform(
                post("/api/v1/sesiones").contentType(MediaType.APPLICATION_JSON).content(json));
    }
}
