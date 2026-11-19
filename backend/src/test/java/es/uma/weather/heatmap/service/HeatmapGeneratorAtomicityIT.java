package es.uma.weather.heatmap.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import es.uma.weather.TestcontainersConfiguration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Clase aparte de {@link HeatmapGeneratorIT}: necesita un punto con un nombre inválido y los puntos
 * se registran para toda la clase, así que cambiarían las expectativas de las demás pruebas.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Import(TestcontainersConfiguration.class)
@DisplayName("HeatmapGenerator: la sustitución del mapa de calor es atómica")
class HeatmapGeneratorAtomicityIT {

    /** zone_name es VARCHAR(100): este nombre hace fallar el INSERT. */
    private static final String TOO_LONG_NAME = "X".repeat(101);

    private static final Instant T1 = Instant.parse("2026-07-20T10:00:00Z");

    @Autowired private HeatmapGenerator generator;
    @Autowired private JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("weather.heat.points[0].name", () -> "Centro");
        registry.add("weather.heat.points[0].latitude", () -> "36.72");
        registry.add("weather.heat.points[0].longitude", () -> "-4.42");
        registry.add("weather.heat.points[1].name", () -> TOO_LONG_NAME);
        registry.add("weather.heat.points[1].latitude", () -> "36.73");
        registry.add("weather.heat.points[1].longitude", () -> "-4.47");
    }

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.update("DELETE FROM zone_heat");
        jdbcTemplate.update("DELETE FROM weather_observations");
    }

    @Test
    @DisplayName("si falla una inserción, conserva entero el mapa anterior")
    void generate_insertFailsAfterAnotherSucceeded_keepsThePreviousMapIntact() {
        // Arrange
        insertZoneHeat("Antigua", 36.70, -4.40, 20.0);
        insertObservation(36.72, -4.42, 60.0);
        insertObservation(36.73, -4.47, 40.0);

        // Act
        Throwable thrown = catchThrowable(generator::generate);

        // Assert
        assertThat(thrown).isInstanceOf(DataAccessException.class);
        assertThat(storedZoneNames()).containsExactly("Antigua");
        assertThat(storedHeat("Antigua")).isEqualTo(20.0);
    }

    private void insertObservation(double latitude, double longitude, double heat) {
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
                OffsetDateTime.ofInstant(T1, ZoneOffset.UTC),
                heat,
                longitude,
                latitude);
    }

    private void insertZoneHeat(String name, double latitude, double longitude, double heat) {
        OffsetDateTime at = OffsetDateTime.ofInstant(T1, ZoneOffset.UTC);
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

    private List<String> storedZoneNames() {
        return jdbcTemplate.queryForList("SELECT zone_name FROM zone_heat", String.class);
    }
}
