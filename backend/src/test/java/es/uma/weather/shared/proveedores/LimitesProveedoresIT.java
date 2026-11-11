package es.uma.weather.shared.proveedores;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/** Cada proveedor de datos externo tiene su límite de peticiones en application.yaml. */
@SpringBootTest
@Import(es.uma.weather.TestcontainersConfiguration.class)
class LimitesProveedoresIT {

    @Autowired private LimitadorPeticiones limitador;

    @ParameterizedTest
    @ValueSource(strings = {"open-meteo", "aemet", "ayuntamiento-malaga"})
    void cadaProveedorTieneSuLimite(String proveedor) {
        assertThat(limitador.permitir(proveedor)).isTrue();
    }
}
