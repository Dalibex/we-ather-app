package es.uma.weather.heatmap.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("ValidatedReport: la valoración debe estar entre 1 y 5")
class ValidatedReportTest {

    private static final double LAT = 36.7213;
    private static final double LON = -4.4214;

    @Nested
    @DisplayName("cuando la valoración es válida")
    class WhenRatingIsValid {

        @ParameterizedTest(name = "valoración {0}")
        @DisplayName("acepta los extremos del rango y conserva la valoración")
        @ValueSource(ints = {1, 5})
        void constructor_ratingAtBoundary_keepsRating(int rating) {
            // Arrange: la valoración llega por parámetro

            // Act
            ValidatedReport report = new ValidatedReport(LAT, LON, rating);

            // Assert
            assertThat(report.rating()).isEqualTo(rating);
        }
    }

    @Nested
    @DisplayName("cuando la valoración está fuera de rango")
    class WhenRatingIsOutOfRange {

        @ParameterizedTest(name = "valoración {0}")
        @DisplayName("lanza IllegalArgumentException justo por debajo y por encima del rango")
        @ValueSource(ints = {0, 6})
        void constructor_ratingOutOfRange_throwsIllegalArgument(int rating) {
            // Arrange: la valoración llega por parámetro

            // Act
            Throwable thrown = catchThrowable(() -> new ValidatedReport(LAT, LON, rating));

            // Assert
            assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        }
    }
}
