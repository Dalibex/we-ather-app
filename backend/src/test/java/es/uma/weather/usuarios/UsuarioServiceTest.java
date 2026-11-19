package es.uma.weather.usuarios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

class UsuarioServiceTest {

    private static final Correo CORREO = new Correo("usuario@example.com");

    private final UsuarioRepository repositorio = mock(UsuarioRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final UsuarioService servicio = new UsuarioService(repositorio, passwordEncoder);

    @Test
    void registraUnUsuarioNuevoGuardandoElHashDeLaContrasena() {
        when(repositorio.existsByCorreo(CORREO)).thenReturn(false);
        when(passwordEncoder.encode("secreta1")).thenReturn("hash");

        Usuario usuario = servicio.registerUser(CORREO, "secreta1", true);

        assertThat(usuario.getCorreo()).isEqualTo(CORREO);
        assertThat(usuario.getHashContrasena()).isEqualTo("hash");
        verify(repositorio).saveAndFlush(usuario);
    }

    @Test
    void siElCorreoYaExisteLanzaCorreoYaRegistrado() {
        when(repositorio.existsByCorreo(CORREO)).thenReturn(true);

        assertThatThrownBy(() -> servicio.registerUser(CORREO, "secreta1", true))
                .isInstanceOf(CorreoYaRegistradoException.class)
                .hasMessage("El correo ya está registrado");
        verify(repositorio).existsByCorreo(CORREO);
        verifyNoMoreInteractions(repositorio);
    }

    @Test
    void siLaBaseDeDatosRechazaElCorreoPorUnRegistroConcurrenteLanzaCorreoYaRegistrado() {
        when(repositorio.existsByCorreo(CORREO)).thenReturn(false);
        when(passwordEncoder.encode("secreta1")).thenReturn("hash");
        when(repositorio.saveAndFlush(any(Usuario.class)))
                .thenThrow(
                        new DataIntegrityViolationException(
                                "no se pudo guardar",
                                new SQLException(
                                        "ERROR: duplicate key value violates unique constraint"
                                                + " \"usuarios_correo_unico\"")));

        assertThatThrownBy(() -> servicio.registerUser(CORREO, "secreta1", true))
                .isInstanceOf(CorreoYaRegistradoException.class)
                .hasMessage("El correo ya está registrado");
    }

    @Test
    void otraViolacionDeIntegridadNoSeTomaPorUnCorreoDuplicado() {
        DataIntegrityViolationException otra =
                new DataIntegrityViolationException(
                        "no se pudo guardar",
                        new SQLException("ERROR: value too long for type character varying(254)"));
        when(repositorio.existsByCorreo(CORREO)).thenReturn(false);
        when(passwordEncoder.encode("secreta1")).thenReturn("hash");
        when(repositorio.saveAndFlush(any(Usuario.class))).thenThrow(otra);

        assertThatThrownBy(() -> servicio.registerUser(CORREO, "secreta1", true)).isSameAs(otra);
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "sin-arroba.example.com", "a@b", "a b@example.com", "@x.com"})
    void rechazaUnCorreoConFormatoInvalido(String valor) {
        Correo correo = new Correo(valor);

        assertThatThrownBy(() -> servicio.registerUser(correo, "secreta1", true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El correo no tiene un formato válido");
        verifyNoInteractions(repositorio);
    }

    @Test
    void rechazaUnCorreoDeMasDe254Caracteres() {
        Correo largo = new Correo("a".repeat(243) + "@example.com"); // 255 caracteres

        assertThatThrownBy(() -> servicio.registerUser(largo, "secreta1", true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El correo no puede superar los 254 caracteres");
        verifyNoInteractions(repositorio);
    }

    @Test
    void aceptaUnCorreoDeExactamente254Caracteres() {
        Correo limite = new Correo("a".repeat(242) + "@example.com"); // 254 caracteres
        when(passwordEncoder.encode("secreta1")).thenReturn("hash");

        assertThat(servicio.registerUser(limite, "secreta1", true).getCorreo()).isEqualTo(limite);
    }

    @Test
    void rechazaUnaContrasenaDeMenosDe8Caracteres() {
        assertThatThrownBy(() -> servicio.registerUser(CORREO, "1234567", true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La contraseña debe tener al menos 8 caracteres");
        verifyNoInteractions(repositorio);
    }

    @Test
    void laLongitudDeLaContrasenaSeCuentaEnPuntosDeCodigo() {
        String sieteEmojis = "😀".repeat(7);
        String ochoEmojis = "😀".repeat(8);
        when(passwordEncoder.encode(ochoEmojis)).thenReturn("hash");

        assertThatThrownBy(() -> servicio.registerUser(CORREO, sieteEmojis, true))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(servicio.registerUser(CORREO, ochoEmojis, true)).isNotNull();
    }
}
