package es.uma.weather.usuarios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class UsuarioTest {

    private static final Correo CORREO = new Correo("usuario@example.com");
    private static final String HASH = "{bcrypt}hash-de-ejemplo";
    private static final Instant ACEPTADA = Instant.parse("2026-11-10T10:00:00Z");

    @Test
    void guardaLosDatosDeLaCuenta() {
        var usuario = Usuario.crear(CORREO, HASH, ACEPTADA);

        assertThat(usuario.getCorreo()).isEqualTo(CORREO);
        assertThat(usuario.getHashContrasena()).isEqualTo(HASH);
        assertThat(usuario.getPrivacidadAceptadaEn()).isEqualTo(ACEPTADA);
    }

    @Test
    void exigeElCorreoElHashYLaAceptacionDeLaPrivacidad() {
        assertThatThrownBy(() -> Usuario.crear(null, HASH, ACEPTADA))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Usuario.crear(CORREO, null, ACEPTADA))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Usuario.crear(CORREO, HASH, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void rechazaUnHashDeContrasenaEnBlanco() {
        assertThatThrownBy(() -> Usuario.crear(CORREO, " ", ACEPTADA))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
