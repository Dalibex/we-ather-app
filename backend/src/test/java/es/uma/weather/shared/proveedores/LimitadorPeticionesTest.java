package es.uma.weather.shared.proveedores;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

/**
 * El limitador deja pasar las peticiones hasta el límite de cada proveedor dentro de su ventana.
 */
@ExtendWith(OutputCaptureExtension.class)
class LimitadorPeticionesTest {

    private final RelojManual reloj = new RelojManual(Instant.parse("2026-11-11T10:00:00Z"));

    private final LimitadorPeticiones limitador =
            new LimitadorPeticiones(
                    Map.of(
                            "open-meteo", new Limite(2, Duration.ofHours(1)),
                            "aemet", new Limite(1, Duration.ofHours(1))),
                    reloj);

    @Test
    void permiteLasPeticionesHastaElLimite() {
        assertThat(limitador.permitir("open-meteo")).isTrue();
        assertThat(limitador.permitir("open-meteo")).isTrue();
    }

    @Test
    void rechazaLaPeticionQueSuperaElLimite() {
        limitador.permitir("open-meteo");
        limitador.permitir("open-meteo");

        assertThat(limitador.permitir("open-meteo")).isFalse();
    }

    @Test
    void vuelveAPermitirCuandoTerminaLaVentana() {
        limitador.permitir("open-meteo");
        limitador.permitir("open-meteo");

        reloj.avanzar(Duration.ofHours(1));

        assertThat(limitador.permitir("open-meteo")).isTrue();
    }

    @Test
    void avisaEnElLogAlRechazarUnaPeticion(CapturedOutput salida) {
        limitador.permitir("aemet");

        limitador.permitir("aemet");

        assertThat(salida).contains("WARN").contains("aemet");
    }

    @Test
    void sigueRechazandoHastaQueTerminaLaVentana() {
        limitador.permitir("open-meteo");
        limitador.permitir("open-meteo");

        reloj.avanzar(Duration.ofMinutes(59));

        assertThat(limitador.permitir("open-meteo")).isFalse();
    }

    @Test
    void cuentaCadaProveedorPorSeparado() {
        limitador.permitir("aemet");

        assertThat(limitador.permitir("aemet")).isFalse();
        assertThat(limitador.permitir("open-meteo")).isTrue();
    }

    @Test
    void rechazaUnProveedorSinLimiteConfigurado() {
        assertThatThrownBy(() -> limitador.permitir("desconocido"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("desconocido");
    }

    /** Reloj que solo avanza cuando la prueba lo pide. */
    private static final class RelojManual extends Clock {

        private Instant ahora;

        RelojManual(Instant inicio) {
            this.ahora = inicio;
        }

        void avanzar(Duration tiempo) {
            ahora = ahora.plus(tiempo);
        }

        @Override
        public Instant instant() {
            return ahora;
        }

        @Override
        public ZoneOffset getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(java.time.ZoneId zona) {
            return this;
        }
    }
}
