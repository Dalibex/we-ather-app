package es.uma.weather.heatmap.service;

import static org.assertj.core.api.Assertions.assertThat;

import es.uma.weather.TestcontainersConfiguration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/** Comprueba contra PostgreSQL/PostGIS real la lectura del mapa de calor para el motor de rutas. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Import(TestcontainersConfiguration.class)
@DisplayName("HeatmapReader: entrega al motor de rutas el último mapa de calor generado")
class HeatmapReaderIT {

    private static final Instant T1 = Instant.parse("2026-07-20T10:00:00Z");
    private static final Instant T2 = Instant.parse("2026-07-20T11:00:00Z");
    private static final Instant T3 = Instant.parse("2026-07-20T11:15:00Z");

    @Autowired private HeatmapReader reader;
    @Autowired private HeatmapGenerator generator;
    @Autowired private JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("weather.heat.points[0].name", () -> "Centro");
        registry.add("weather.heat.points[0].latitude", () -> "36.72");
        registry.add("weather.heat.points[0].longitude", () -> "-4.42");
    }

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.update("DELETE FROM zone_heat");
        jdbcTemplate.update("DELETE FROM weather_observations");
    }

    @Test
    @DisplayName("sin mapa generado todavía, no devuelve ninguno")
    void latest_noZones_returnsEmpty() {
        // Act
        Optional<HeatmapSnapshot> snapshot = reader.latest();

        // Assert
        assertThat(snapshot).isEmpty();
    }

    @Test
    @DisplayName("devuelve las zonas por nombre, con su calor y coordenadas")
    void latest_storedZones_returnsThemSortedByName() {
        // Arrange
        insertZoneHeat("Teatinos", 36.73, -4.47, 40.0, T2, T3);
        insertZoneHeat("Centro", 36.72, -4.42, 60.0, T2, T3);

        // Act
        HeatmapSnapshot snapshot = reader.latest().orElseThrow();

        // Assert
        assertThat(snapshot.zones())
                .containsExactly(
                        new ZoneHeat("Centro", 36.72, -4.42, 60.0),
                        new ZoneHeat("Teatinos", 36.73, -4.47, 40.0));
        assertThat(snapshot.generatedAt()).isEqualTo(T3);
    }

    @Test
    @DisplayName("la fecha del dato es la observación más antigua de las zonas")
    void latest_zonesWithDifferentObservations_returnsOldestObservedAt() {
        // Arrange
        insertZoneHeat("Centro", 36.72, -4.42, 60.0, T2, T3);
        insertZoneHeat("Teatinos", 36.73, -4.47, 40.0, T1, T3);

        // Act
        HeatmapSnapshot snapshot = reader.latest().orElseThrow();

        // Assert
        assertThat(snapshot.observedAt()).isEqualTo(T1);
    }

    @Test
    @DisplayName("lee el mapa que acaba de guardar la generación periódica")
    void latest_afterGeneration_returnsGeneratedHeat() {
        // Arrange
        insertObservation(36.72, -4.42, T1, 55.0);
        generator.generate();

        // Act
        HeatmapSnapshot snapshot = reader.latest().orElseThrow();

        // Assert
        assertThat(snapshot.zones()).containsExactly(new ZoneHeat("Centro", 36.72, -4.42, 55.0));
        assertThat(snapshot.observedAt()).isEqualTo(T1);
        assertThat(snapshot.heatAt(36.7213, -4.4214)).isEqualTo(55.0);
    }

    private void insertObservation(
            double latitude, double longitude, Instant observedAt, double heat) {
        jdbcTemplate.update(
                """
                INSERT INTO weather_observations
                    (source, grid_name, latitude, longitude, observed_at, temperature_c,
                     relative_humidity_pct, wind_speed_kmh, shortwave_radiation_wm2, uv_index,
                     heat_score, heat_level, location)
                VALUES ('OPEN_METEO', 'test', ?, ?, ?, 30, 50, 5, 500, 5, ?, 'MEDIUM',
                        ST_SetSRID(ST_MakePoint(?, ?), 4326)::geography)
                """,
                latitude,
                longitude,
                OffsetDateTime.ofInstant(observedAt, ZoneOffset.UTC),
                heat,
                longitude,
                latitude);
    }

    private void insertZoneHeat(
            String name,
            double latitude,
            double longitude,
            double heat,
            Instant observedAt,
            Instant generatedAt) {
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
                OffsetDateTime.ofInstant(observedAt, ZoneOffset.UTC),
                OffsetDateTime.ofInstant(generatedAt, ZoneOffset.UTC));
    }
}
