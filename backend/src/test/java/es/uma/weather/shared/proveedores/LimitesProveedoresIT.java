package es.uma.weather.shared.proveedores;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/** Cada proveedor de datos externo tiene su límite de peticiones en application.yaml. */
@SpringBootTest
@Import(es.uma.weather.TestcontainersConfiguration.class)
class LimitesProveedoresIT {

    @Autowired private LimitesProveedoresProperties properties;

    @Test
    void cadaProveedorTieneSuLimite() {
        assertThat(properties.limites())
                .containsOnlyKeys("open-meteo", "aemet", "ayuntamiento-malaga")
                .containsEntry("open-meteo", new Limite(400, Duration.ofHours(1)))
                .containsEntry("aemet", new Limite(40, Duration.ofHours(1)))
                .containsEntry("ayuntamiento-malaga", new Limite(40, Duration.ofHours(1)));
    }
}
