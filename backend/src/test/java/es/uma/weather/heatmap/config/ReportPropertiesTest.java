package es.uma.weather.heatmap.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Un peso mal configurado se detecta al arrancar: 0 (propiedad ausente) daría 0/0 = NaN. */
@DisplayName("ReportProperties: el peso de la estimación debe ser positivo")
class ReportPropertiesTest {

    @Nested
    @DisplayName("cuando el peso no es positivo")
    class WhenWeightIsNotPositive {

        @ParameterizedTest(name = "peso {0}")
        @DisplayName("lanza IllegalArgumentException con cero y con un valor negativo")
        @ValueSource(ints = {0, -1})
        void constructor_nonPositiveWeight_throwsIllegalArgument(int pesoEstimacion) {
            // Arrange: el peso llega por parámetro

            // Act
            Throwable thrown = catchThrowable(() -> new ReportProperties(pesoEstimacion));

            // Assert
            assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("cuando el peso es positivo")
    class WhenWeightIsPositive {

        @ParameterizedTest(name = "peso {0}")
        @DisplayName("acepta el mínimo positivo y conserva el valor")
        @ValueSource(ints = {1})
        void constructor_positiveWeight_keepsWeight(int pesoEstimacion) {
            // Arrange: el peso llega por parámetro

            // Act
            ReportProperties properties = new ReportProperties(pesoEstimacion);

            // Assert
            assertThat(properties.pesoEstimacion()).isEqualTo(pesoEstimacion);
        }
    }
}
