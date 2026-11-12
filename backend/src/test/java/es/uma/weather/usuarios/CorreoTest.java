package es.uma.weather.usuarios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class CorreoTest {

    @Test
    void quitaLosEspaciosYPasaAMinusculas() {
        var correo = new Correo("  Usuario@Example.COM ");

        assertThat(correo.valor()).isEqualTo("usuario@example.com");
    }

    @Test
    void dosFormasDeEscribirElMismoCorreoSonElMismoCorreo() {
        assertThat(new Correo("USUARIO@example.com")).isEqualTo(new Correo("usuario@example.com"));
    }

    @Test
    void rechazaUnCorreoNulo() {
        assertThatThrownBy(() -> new Correo(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void rechazaUnCorreoEnBlanco() {
        assertThatThrownBy(() -> new Correo("   ")).isInstanceOf(IllegalArgumentException.class);
    }
}
