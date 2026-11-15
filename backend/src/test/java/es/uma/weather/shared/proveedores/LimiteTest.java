package es.uma.weather.shared.proveedores;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import org.junit.jupiter.api.Test;

/** Un límite mal configurado se detecta al arrancar, no al llamar al proveedor. */
class LimiteTest {

    @Test
    void rechazaUnMaximoDePeticionesNoPositivo() {
        assertThatThrownBy(() -> new Limite(0, Duration.ofHours(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rechazaUnaVentanaNoPositiva() {
        assertThatThrownBy(() -> new Limite(10, Duration.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
