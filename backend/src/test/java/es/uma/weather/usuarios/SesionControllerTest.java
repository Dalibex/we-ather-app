package es.uma.weather.usuarios;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import es.uma.weather.usuarios.registro.AutenticacionService;
import es.uma.weather.usuarios.registro.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** Fallos del inicio de sesión que no dependen de las credenciales. */
@WebMvcTest(SesionController.class)
@AutoConfigureMockMvc(addFilters = false)
class SesionControllerTest {

    @Autowired private MockMvc mvc;

    @MockitoBean private AutenticacionService autenticacionService;

    // Dependencias del filtro JWT, que @WebMvcTest carga aunque aquí no se use.
    @MockitoBean private JwtService jwtService;
    @MockitoBean private UsuarioService usuarioService;

    // Si falla la base de datos, Spring Security lo envuelve en una AuthenticationException. No
    // debe responderse como unas credenciales incorrectas, sino como un error del servidor.
    @Test
    void unFalloInternoNoSeRespondeComoCredencialesIncorrectas() {
        when(autenticacionService.autenticar("usuario@example.com", "ContrasenaSegura123!"))
                .thenThrow(new InternalAuthenticationServiceException("base de datos caída"));

        assertThatThrownBy(
                        () ->
                                mvc.perform(
                                        post("/api/v1/sesiones")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(
                                                        """
                                                        {"correo": "usuario@example.com",
                                                         "contrasena": "ContrasenaSegura123!"}
                                                        """)))
                .hasRootCauseInstanceOf(InternalAuthenticationServiceException.class);
    }
}
