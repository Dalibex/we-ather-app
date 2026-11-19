package es.uma.weather.heatmap.service;

import static org.assertj.core.api.Assertions.assertThat;

import es.uma.weather.TestcontainersConfiguration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/** Comprueba la generación del mapa de calor contra PostgreSQL/PostGIS real. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Import({TestcontainersConfiguration.class, HeatmapGeneratorIT.FakeReportSourceConfig.class})
@DisplayName("HeatmapGenerator: genera el mapa de calor por zonas")
class HeatmapGeneratorIT {

    private static final Instant T1 = Instant.parse("2026-07-20T10:00:00Z");
    private static final Instant T2 = Instant.parse("2026-07-20T11:00:00Z");
    private static final Instant T3 = Instant.parse("2026-07-20T12:00:00Z");

    @Autowired private HeatmapGenerator generator;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private FakeReportSource reportSource;

    /**
     * Fake (no mock): una fuente en memoria que devuelve los reportes que la prueba le ponga. Es
     * más simple que un stub de Mockito y no obliga a verificar llamadas.
     */
    static class FakeReportSource implements ValidatedReportSource {
        private final List<ValidatedReport> reports = new ArrayList<>();

        @Override
        public List<ValidatedReport> currentValidatedReports() {
            return List.copyOf(reports);
        }
    }

    @TestConfiguration
    static class FakeReportSourceConfig {
        @Bean
        @Primary
        FakeReportSource fakeReportSource() {
            return new FakeReportSource();
        }
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("weather.heat.points[0].name", () -> "Centro");
        registry.add("weather.heat.points[0].latitude", () -> "36.72");
        registry.add("weather.heat.points[0].longitude", () -> "-4.42");
        registry.add("weather.heat.points[1].name", () -> "Teatinos");
        registry.add("weather.heat.points[1].latitude", () -> "36.73");
        registry.add("weather.heat.points[1].longitude", () -> "-4.47");
    }

    @BeforeEach
    void cleanDatabaseAndReports() {
        jdbcTemplate.update("DELETE FROM zone_heat");
        jdbcTemplate.update("DELETE FROM weather_observations");
        reportSource.reports.clear();
    }

    @Nested
    @DisplayName("con observaciones de Open-Meteo")
    class WithObservations {

        @Test
        @DisplayName("guarda una fila por punto con su calor y la hora de su observación")
        void generate_twoPointsWithObservations_returnsTwoAndStoresBothRows() {
            // Arrange
            insertObservation("OPEN_METEO", 36.72, -4.42, T1, 60.0);
            insertObservation("OPEN_METEO", 36.73, -4.47, T2, 40.0);

            // Act
            int saved = generator.generate();

            // Assert
            assertThat(saved).isEqualTo(2);
            assertThat(storedHeat("Centro")).isEqualTo(60.0);
            assertThat(storedObservedAt("Centro")).isEqualTo(T1);
            assertThat(storedHeat("Teatinos")).isEqualTo(40.0);
            assertThat(storedObservedAt("Teatinos")).isEqualTo(T2);
        }

        @Test
        @DisplayName("usa la observación más reciente del punto")
        void generate_pointWithTwoObservations_usesTheLatest() {
            // Arrange
            insertObservation("OPEN_METEO", 36.72, -4.42, T1, 30.0);
            insertObservation("OPEN_METEO", 36.72, -4.42, T3, 70.0);
            insertObservation("OPEN_METEO", 36.72, -4.42, T2, 50.0);

            // Act
            int saved = generator.generate();

            // Assert
            assertThat(saved).isEqualTo(1);
            assertThat(storedHeat("Centro")).isEqualTo(70.0);
            assertThat(storedObservedAt("Centro")).isEqualTo(T3);
        }

        @Test
        @DisplayName("ignora observaciones de otra fuente aunque sean más recientes")
        void generate_newerObservationFromAnotherSource_usesOpenMeteo() {
            // Arrange
            insertObservation("OPEN_METEO", 36.72, -4.42, T1, 55.0);
            insertObservation("AEMET", 36.72, -4.42, T3, 90.0);

            // Act
            int saved = generator.generate();

            // Assert
            assertThat(saved).isEqualTo(1);
            assertThat(storedHeat("Centro")).isEqualTo(55.0);
            assertThat(storedObservedAt("Centro")).isEqualTo(T1);
        }

        @Test
        @DisplayName("Fake: ajusta con los reportes la zona más cercana y no toca la otra")
        void generate_reportsNearOneZone_storesCombinedHeatForThatZoneOnly() {
            // Arrange
            insertObservation("OPEN_METEO", 36.72, -4.42, T1, 60.0);
            insertObservation("OPEN_METEO", 36.73, -4.47, T1, 40.0);
            reportSource.reports.add(new ValidatedReport(36.72, -4.42, 5));
            reportSource.reports.add(new ValidatedReport(36.721, -4.421, 5));

            // Act
            int saved = generator.generate();

            // Assert
            // (5 * 60 + 100 + 100) / (5 + 2) = 71.43 -> 71.4
            assertThat(saved).isEqualTo(2);
            assertThat(storedHeat("Centro")).isEqualTo(71.4);
            assertThat(storedHeat("Teatinos")).isEqualTo(40.0);
        }

        @Test
        @DisplayName("omite el punto sin observación y guarda el otro")
        void generate_onePointWithoutObservation_returnsOneAndStoresOnlyTheOther() {
            // Arrange
            insertObservation("OPEN_METEO", 36.73, -4.47, T1, 40.0);

            // Act
            int saved = generator.generate();

            // Assert
            assertThat(saved).isEqualTo(1);
            assertThat(storedZoneNames()).containsExactly("Teatinos");
        }

        @Test
        @DisplayName("sustituye entera la generación anterior: la zona que ya no sale desaparece")
        void generate_previousRowOfRemovedZone_deletesIt() {
            // Arrange
            insertZoneHeat("Antigua", 36.70, -4.40, 20.0, T1);
            insertObservation("OPEN_METEO", 36.72, -4.42, T2, 60.0);
            insertObservation("OPEN_METEO", 36.73, -4.47, T2, 40.0);

            // Act
            int saved = generator.generate();

            // Assert
            assertThat(saved).isEqualTo(2);
            assertThat(storedZoneNames()).containsExactlyInAnyOrder("Centro", "Teatinos");
        }
    }

    @Nested
    @DisplayName("sin ninguna observación de Open-Meteo")
    class WithoutObservations {

        @Test
        @DisplayName("devuelve 0 y conserva las filas anteriores")
        void generate_noObservationsAtAll_returnsZeroAndKeepsPreviousRows() {
            // Arrange
            insertZoneHeat("Antigua", 36.70, -4.40, 20.0, T1);
            insertZoneHeat("Centro", 36.72, -4.42, 45.0, T1);

            // Act
            int saved = generator.generate();

            // Assert
            assertThat(saved).isZero();
            assertThat(storedZoneNames()).containsExactlyInAnyOrder("Antigua", "Centro");
            assertThat(storedHeat("Antigua")).isEqualTo(20.0);
            assertThat(storedHeat("Centro")).isEqualTo(45.0);
        }
    }

    private void insertObservation(
            String source, double latitude, double longitude, Instant observedAt, double heat) {
        jdbcTemplate.update(
                """
                INSERT INTO weather_observations
                    (source, grid_name, latitude, longitude, observed_at, temperature_c,
                     relative_humidity_pct, wind_speed_kmh, shortwave_radiation_wm2, uv_index,
                     heat_score, heat_level, location)
                VALUES (?, 'test', ?, ?, ?, 30, 50, 5, 500, 5, ?, 'MEDIUM',
                        ST_SetSRID(ST_MakePoint(?, ?), 4326)::geography)
                """,
                source,
                latitude,
                longitude,
                OffsetDateTime.ofInstant(observedAt, ZoneOffset.UTC),
                heat,
                longitude,
                latitude);
    }

    private void insertZoneHeat(
            String name, double latitude, double longitude, double heat, Instant observedAt) {
        OffsetDateTime at = OffsetDateTime.ofInstant(observedAt, ZoneOffset.UTC);
        jdbcTemplate.update(
                """
                INSERT INTO zone_heat (zone_name, latitude, longitude, heat_score, observed_at,
                                       generated_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                name,
                latitude,
                longitude,
                heat,
                at,
                at);
    }

    private double storedHeat(String zoneName) {
        return jdbcTemplate.queryForObject(
                "SELECT heat_score FROM zone_heat WHERE zone_name = ?", Double.class, zoneName);
    }

    private Instant storedObservedAt(String zoneName) {
        return jdbcTemplate
                .queryForObject(
                        "SELECT observed_at FROM zone_heat WHERE zone_name = ?",
                        OffsetDateTime.class,
                        zoneName)
                .toInstant();
    }

    private List<String> storedZoneNames() {
        return jdbcTemplate.queryForList("SELECT zone_name FROM zone_heat", String.class);
    }
}
