package es.uma.weather.ingestion.service;

import static org.assertj.core.api.Assertions.assertThat;

import es.uma.weather.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

/** Comprueba contra PostgreSQL/PostGIS real cuándo se consideran desactualizadas las calles. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Import(TestcontainersConfiguration.class)
@DisplayName("UrbanDataIngestionService.isOutdated: frescura de las calles guardadas")
class UrbanDataFreshnessIT {

    @Autowired private UrbanDataIngestionService urbanDataIngestionService;
    @Autowired private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanStreetSegments() {
        jdbcTemplate.update("DELETE FROM street_segments");
    }

    @Test
    @DisplayName("sin calles guardadas está desactualizado")
    void isOutdated_noStreetSegments_returnsTrue() {
        // Arrange: tabla vacía por el @BeforeEach

        // Act
        boolean outdated = urbanDataIngestionService.isOutdated();

        // Assert
        assertThat(outdated).isTrue();
    }

    @Test
    @DisplayName("con la última actualización de hace un día no está desactualizado")
    void isOutdated_latestUpdateOneDayAgo_returnsFalse() {
        // Arrange
        insertSegment(1L, "1 day");

        // Act
        boolean outdated = urbanDataIngestionService.isOutdated();

        // Assert
        assertThat(outdated).isFalse();
    }

    @Test
    @DisplayName("con la última actualización de hace ocho días está desactualizado")
    void isOutdated_latestUpdateEightDaysAgo_returnsTrue() {
        // Arrange
        insertSegment(1L, "8 days");

        // Act
        boolean outdated = urbanDataIngestionService.isOutdated();

        // Assert
        assertThat(outdated).isTrue();
    }

    @Test
    @DisplayName("usa la actualización más reciente: una calle vieja y otra reciente no lo está")
    void isOutdated_oldAndRecentSegments_usesMostRecentAndReturnsFalse() {
        // Arrange
        insertSegment(1L, "8 days");
        insertSegment(2L, "1 day");

        // Act
        boolean outdated = urbanDataIngestionService.isOutdated();

        // Assert
        assertThat(outdated).isFalse();
    }

    /**
     * Guarda una calle cuya última actualización fue hace el intervalo indicado (calculado en SQL).
     */
    private void insertSegment(long osmWayId, String age) {
        jdbcTemplate.update(
                """
                INSERT INTO street_segments (osm_way_id, highway, name, geom, updated_at)
                VALUES (?, 'residential', 'x',
                        ST_GeomFromText('LINESTRING(-4.42 36.72,-4.41 36.73)', 4326),
                        NOW() - CAST(? AS INTERVAL))
                """,
                osmWayId,
                age);
    }
}
