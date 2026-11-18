package es.uma.weather.heatmap.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import es.uma.weather.heatmap.config.ReportProperties;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("ReportCombiner: combina los reportes de usuarios con el calor estimado de una zona")
class ReportCombinerTest {

    private static final double LAT = 36.7213;
    private static final double LON = -4.4214;

    private final ReportCombiner combiner = new ReportCombiner(new ReportProperties(5));
    private final ZoneHeat centro = new ZoneHeat("Centro", LAT, LON, 40.0);

    private static ValidatedReport reportOf(int rating) {
        return new ValidatedReport(LAT, LON, rating);
    }

    @Nested
    @DisplayName("cuando la zona no tiene reportes")
    class WhenNoReports {

        @Test
        @DisplayName("conserva el calor estimado, el nombre y las coordenadas")
        void combine_noReports_keepsZoneUnchanged() {
            // Arrange
            List<ValidatedReport> reports = Collections.emptyList();

            // Act
            List<ZoneHeat> result = combiner.combine(List.of(centro), reports);

            // Assert
            assertThat(result).hasSize(1);
            ZoneHeat zone = result.get(0);
            assertThat(zone.name()).isEqualTo("Centro");
            assertThat(zone.latitude()).isEqualTo(LAT);
            assertThat(zone.longitude()).isEqualTo(LON);
            assertThat(zone.heatScore()).isCloseTo(40.0, within(0.001));
        }
    }

    @Nested
    @DisplayName("cuando la zona tiene un solo reporte")
    class WhenOneReport {

        @ParameterizedTest(name = "valoración {0} -> calor {1}")
        @DisplayName("mezcla la valoración con la estimación según su peso")
        @CsvSource({"1, 33.3", "2, 37.5", "3, 41.7", "4, 45.8", "5, 50.0"})
        void combine_oneReport_blendsRatingWithEstimate(int rating, double expected) {
            // Arrange
            List<ValidatedReport> reports = List.of(reportOf(rating));

            // Act
            List<ZoneHeat> result = combiner.combine(List.of(centro), reports);

            // Assert
            assertThat(result).hasSize(1);
            ZoneHeat zone = result.get(0);
            assertThat(zone.name()).isEqualTo("Centro");
            assertThat(zone.latitude()).isEqualTo(LAT);
            assertThat(zone.longitude()).isEqualTo(LON);
            assertThat(zone.heatScore()).isCloseTo(expected, within(0.001));
        }
    }

    @Nested
    @DisplayName("cuando la zona tiene varios reportes")
    class WhenSeveralReports {

        @Test
        @DisplayName(
                "con tantos reportes como el peso k, queda a medio camino entre la estimación y"
                        + " los reportes")
        void combine_asManyReportsAsWeight_landsHalfway() {
            // Arrange
            List<ValidatedReport> reports =
                    List.of(reportOf(5), reportOf(5), reportOf(5), reportOf(5), reportOf(5));

            // Act
            List<ZoneHeat> result = combiner.combine(List.of(centro), reports);

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).heatScore()).isCloseTo(70.0, within(0.001));
        }

        @Test
        @DisplayName("promedia las valoraciones: no se queda con la última ni con la máxima")
        void combine_mixedRatings_averagesThem() {
            // Arrange
            List<ValidatedReport> reports = List.of(reportOf(1), reportOf(5));

            // Act
            List<ZoneHeat> result = combiner.combine(List.of(centro), reports);

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).heatScore()).isCloseTo(42.9, within(0.001));
        }
    }

    @Nested
    @DisplayName("cuando hay varias zonas")
    class WhenSeveralZones {

        private final ZoneHeat teatinos = new ZoneHeat("Teatinos", 36.7160, -4.4720, 60.0);

        @Test
        @DisplayName("un reporte cerca de Teatinos solo ajusta Teatinos y deja Centro igual")
        void combine_reportNearTeatinos_adjustsOnlyTeatinos() {
            // Arrange
            List<ValidatedReport> reports = List.of(new ValidatedReport(36.7170, -4.4700, 5));

            // Act
            List<ZoneHeat> result = combiner.combine(List.of(centro, teatinos), reports);

            // Assert
            assertThat(result).extracting(ZoneHeat::name).containsExactly("Centro", "Teatinos");
            assertThat(result.get(0).heatScore()).isCloseTo(40.0, within(0.001));
            assertThat(result.get(1).heatScore()).isCloseTo(66.7, within(0.001));
        }

        @Test
        @DisplayName("un reporte cerca de cada zona ajusta cada zona con el suyo")
        void combine_oneReportPerZone_adjustsEachZoneWithItsOwn() {
            // Arrange
            List<ValidatedReport> reports =
                    List.of(
                            new ValidatedReport(36.7220, -4.4200, 1),
                            new ValidatedReport(36.7170, -4.4700, 5));

            // Act
            List<ZoneHeat> result = combiner.combine(List.of(centro, teatinos), reports);

            // Assert
            assertThat(result).extracting(ZoneHeat::name).containsExactly("Centro", "Teatinos");
            assertThat(result.get(0).heatScore()).isCloseTo(33.3, within(0.001));
            assertThat(result.get(1).heatScore()).isCloseTo(66.7, within(0.001));
        }

        @Test
        @DisplayName(
                "la longitud cuenta en la distancia: misma latitud que Teatinos pero longitud"
                        + " de Centro cuenta para Centro")
        void combine_sameLatitudeAsTeatinosButCentroLongitude_adjustsCentro() {
            // Arrange
            List<ValidatedReport> reports = List.of(new ValidatedReport(36.7160, -4.4250, 5));

            // Act
            List<ZoneHeat> result = combiner.combine(List.of(centro, teatinos), reports);

            // Assert
            assertThat(result).extracting(ZoneHeat::name).containsExactly("Centro", "Teatinos");
            assertThat(result.get(0).heatScore()).isCloseTo(50.0, within(0.001));
            assertThat(result.get(1).heatScore()).isCloseTo(60.0, within(0.001));
        }

        @Test
        @DisplayName(
                "la latitud cuenta en la distancia: longitud más cercana a Centro pero latitud"
                        + " de Ciudad Jardín cuenta para Ciudad Jardín")
        void combine_centroLongitudeButCiudadJardinLatitude_adjustsCiudadJardin() {
            // Arrange
            ZoneHeat ciudadJardin = new ZoneHeat("Ciudad Jardín", 36.7390, -4.4300, 60.0);
            List<ValidatedReport> reports = List.of(new ValidatedReport(36.7380, -4.4230, 5));

            // Act
            List<ZoneHeat> result = combiner.combine(List.of(centro, ciudadJardin), reports);

            // Assert
            assertThat(result)
                    .extracting(ZoneHeat::name)
                    .containsExactly("Centro", "Ciudad Jardín");
            assertThat(result.get(0).heatScore()).isCloseTo(40.0, within(0.001));
            assertThat(result.get(1).heatScore()).isCloseTo(66.7, within(0.001));
        }

        @Test
        @DisplayName(
                "la longitud se corrige con el coseno de la latitud: un grado de longitud mide"
                        + " menos que uno de latitud")
        void combine_longitudeGapShrunkByCosLatitude_adjustsCarreteraDeCadiz() {
            // Arrange
            ZoneHeat carreteraDeCadiz = new ZoneHeat("Carretera de Cádiz", 36.7010, -4.4440, 60.0);
            List<ValidatedReport> reports = List.of(new ValidatedReport(36.7010, -4.4230, 5));

            // Act
            List<ZoneHeat> result = combiner.combine(List.of(centro, carreteraDeCadiz), reports);

            // Assert
            assertThat(result)
                    .extracting(ZoneHeat::name)
                    .containsExactly("Centro", "Carretera de Cádiz");
            assertThat(result.get(0).heatScore()).isCloseTo(40.0, within(0.001));
            assertThat(result.get(1).heatScore()).isCloseTo(66.7, within(0.001));
        }

        @Test
        @DisplayName("con dos zonas a igual distancia gana la primera de la lista")
        void combine_reportEquidistantFromTwoZones_adjustsFirstZone() {
            // Arrange
            ZoneHeat primera = new ZoneHeat("Primera", LAT, LON, 40.0);
            ZoneHeat segunda = new ZoneHeat("Segunda", LAT, LON, 40.0);
            List<ValidatedReport> reports = List.of(reportOf(5));

            // Act
            List<ZoneHeat> result = combiner.combine(List.of(primera, segunda), reports);

            // Assert
            assertThat(result).extracting(ZoneHeat::name).containsExactly("Primera", "Segunda");
            assertThat(result.get(0).heatScore()).isCloseTo(50.0, within(0.001));
            assertThat(result.get(1).heatScore()).isCloseTo(40.0, within(0.001));
        }
    }

    @Nested
    @DisplayName("cuando no hay zonas")
    class WhenNoZones {

        @Test
        @DisplayName("devuelve una lista vacía aunque haya reportes")
        void combine_noZones_returnsEmptyList() {
            // Arrange
            List<ValidatedReport> reports = List.of(reportOf(5));

            // Act
            List<ZoneHeat> result = combiner.combine(List.of(), reports);

            // Assert
            assertThat(result).isEmpty();
        }
    }
}
