package es.uma.weather.usuarios;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import es.uma.weather.TestcontainersConfiguration;
import es.uma.weather.usuarios.registro.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class, RegistroJwtIT.EndpointProtegidoTestConfig.class})
@Transactional
class RegistroJwtIT {

    @Autowired private MockMvc mockMvc;

    @Autowired private JwtService jwtService;

    @Value("${jwt.secret}")
    private String secreto;

    private static final String CORREO = "registro-jwt@example.com";
    private static final String CONTRASENA = "ContrasenaSegura123!";

    @Test
    void registraUnUsuarioYAccedeConSuJwt() throws Exception {

        String respuesta =
                registrar(CORREO, CONTRASENA)
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").isNotEmpty())
                        .andExpect(jsonPath("$.correo").value(CORREO))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        String token = jwtService.generateToken(UUID.fromString(JsonPath.read(respuesta, "$.id")));

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
    void noPermiteAccederConUnJwtValidoDeUnUsuarioInexistente() throws Exception {

        String token = jwtService.generateToken(UUID.randomUUID());

        mockMvc.perform(get("/api/test/protegido").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void noPermiteAccederConUnJwtFirmadoConOtraClave() throws Exception {

        SecretKey otraClave = Keys.hmacShaKeyFor(new byte[32]);
        String token =
                Jwts.builder()
                        .subject(registrarYObtenerId())
                        .expiration(new Date(System.currentTimeMillis() + 60_000))
                        .signWith(otraClave)
                        .compact();

        mockMvc.perform(get("/api/test/protegido").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void noPermiteAccederConUnJwtCaducado() throws Exception {

        Date ayer = new Date(System.currentTimeMillis() - 86_400_000);
        String token =
                Jwts.builder()
                        .subject(registrarYObtenerId())
                        .issuedAt(new Date(ayer.getTime() - 60_000))
                        .expiration(ayer)
                        .signWith(claveDePruebas())
                        .compact();

        mockMvc.perform(get("/api/test/protegido").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void noPermiteAccederConUnJwtFirmadoSinSujeto() throws Exception {

        String token =
                Jwts.builder()
                        .expiration(new Date(System.currentTimeMillis() + 60_000))
                        .signWith(claveDePruebas())
                        .compact();

        mockMvc.perform(get("/api/test/protegido").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unJwtInvalidoNoBloqueaUnaRutaPublica() throws Exception {

        mockMvc.perform(
                        post("/api/v1/usuarios")
                                .header("Authorization", "Bearer token.invalido.manipulado")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {"correo": "token-malo@example.com", "contrasena": "%s",
                                         "aceptaPrivacidad": true}
                                        """
                                                .formatted(CONTRASENA)))
                .andExpect(status().isCreated());
    }

    @Test
    void rechazaUnCuerpoConJsonMalformado() throws Exception {

        enviar("{\"correo\": ")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"));
    }

    @Test
    void rechazaUnCuerpoConUnTipoIncorrecto() throws Exception {

        enviar(
                        """
                        {"correo": "tipo@example.com", "contrasena": "%s",
                         "aceptaPrivacidad": "si"}
                        """
                                .formatted(CONTRASENA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"));
    }

    @Test
    void rechazaUnCorreoConFormatoInvalidoYUnaContrasenaCorta() throws Exception {

        registrar("abc", CONTRASENA)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"));
        registrar("corta@example.com", "1234567")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"));
    }

    @Test
    void rechazaUnCorreoDeMasDe254CaracteresConUn400() throws Exception {

        registrar("a".repeat(243) + "@example.com", CONTRASENA)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"));
    }

    @Test
    void elRegistroNoRequiereAutenticacionPrevia() throws Exception {

        registrar("sin-token@example.com", CONTRASENA).andExpect(status().isCreated());
    }

    @Test
    void rechazaElRegistroSiNoSeAceptaLaPrivacidad() throws Exception {

        enviar(
                        """
                        {"correo": "sin-privacidad@example.com", "contrasena": "%s",
                         "aceptaPrivacidad": false}
                        """
                                .formatted(CONTRASENA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"))
                .andExpect(jsonPath("$.mensaje").value("Debes aceptar la política de privacidad"));
    }

    @Test
    void rechazaElRegistroConUnaContrasenaEnBlanco() throws Exception {

        registrar("sin-contrasena@example.com", "   ")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"));
    }

    @Test
    void rechazaElRegistroSinCorreo() throws Exception {

        enviar(
                        """
                        {"contrasena": "%s", "aceptaPrivacidad": true}
                        """
                                .formatted(CONTRASENA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"));
    }

    @Test
    void rechazaElRegistroConUnCorreoEnBlanco() throws Exception {

        registrar("   ", CONTRASENA)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"));
    }

    @Test
    void rechazaElRegistroDeUnCorreoYaRegistrado() throws Exception {

        registrar("duplicado@example.com", CONTRASENA).andExpect(status().isCreated());

        registrar("Duplicado@Example.com", CONTRASENA)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("CORREO_YA_REGISTRADO"))
                .andExpect(jsonPath("$.mensaje").value("El correo ya está registrado"));
    }

    private String registrarYObtenerId() throws Exception {
        String respuesta =
                registrar(CORREO, CONTRASENA)
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        return JsonPath.read(respuesta, "$.id");
    }

    private SecretKey claveDePruebas() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secreto));
    }

    private ResultActions registrar(String correo, String contrasena) throws Exception {
        return enviar(
                """
                {"correo": "%s", "contrasena": "%s", "aceptaPrivacidad": true}
                """
                        .formatted(correo, contrasena));
    }

    private ResultActions enviar(String json) throws Exception {
        return mockMvc.perform(
                post("/api/v1/usuarios").contentType(MediaType.APPLICATION_JSON).content(json));
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
