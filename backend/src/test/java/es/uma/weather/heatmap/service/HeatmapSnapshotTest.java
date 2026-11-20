package es.uma.weather.heatmap.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("HeatmapSnapshot: el mapa de calor que consulta el motor de rutas")
class HeatmapSnapshotTest {

    private static final Instant GENERATED = Instant.parse("2026-07-20T10:15:00Z");
    private static final Instant OBSERVED = Instant.parse("2026-07-20T10:00:00Z");

    private final ZoneHeat centro = new ZoneHeat("Centro", 36.7213, -4.4214, 70.0);
    private final ZoneHeat teatinos = new ZoneHeat("Teatinos", 36.7160, -4.4720, 30.0);

    @Nested
    @DisplayName("heatAt")
    class HeatAt {

        @Test
        @DisplayName("devuelve el calor de la zona más cercana al punto")
        void heatAt_pointNearOneZone_returnsThatZoneHeat() {
            // Arrange
            HeatmapSnapshot snapshot =
                    new HeatmapSnapshot(List.of(centro, teatinos), GENERATED, OBSERVED);

            // Act
            double nearCentro = snapshot.heatAt(36.7200, -4.4200);
            double nearTeatinos = snapshot.heatAt(36.7150, -4.4700);

            // Assert
            assertThat(nearCentro).isEqualTo(70.0);
            assertThat(nearTeatinos).isEqualTo(30.0);
        }

        @Test
        @DisplayName("en un empate devuelve la primera zona, como los reportes")
        void heatAt_pointAtSameDistance_returnsFirstZone() {
            // Arrange
            ZoneHeat oeste = new ZoneHeat("Oeste", 36.72, -4.43, 10.0);
            ZoneHeat este = new ZoneHeat("Este", 36.72, -4.41, 90.0);
            HeatmapSnapshot snapshot =
                    new HeatmapSnapshot(List.of(oeste, este), GENERATED, OBSERVED);

            // Act
            double heat = snapshot.heatAt(36.72, -4.42);

            // Assert
            assertThat(heat).isEqualTo(10.0);
        }
    }

    @Nested
    @DisplayName("al construirlo")
    class Construction {

        @Test
        @DisplayName("rechaza un mapa sin zonas")
        void constructor_noZones_throwsIllegalArgumentException() {
            // Act
            Throwable thrown =
                    catchThrowable(() -> new HeatmapSnapshot(List.of(), GENERATED, OBSERVED));

            // Assert
            assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("guarda una copia de las zonas: cambiar la lista original no le afecta")
        void constructor_originalListChanges_keepsItsOwnCopy() {
            // Arrange
            List<ZoneHeat> zones = new ArrayList<>(List.of(centro));
            HeatmapSnapshot snapshot = new HeatmapSnapshot(zones, GENERATED, OBSERVED);

            // Act
            zones.add(teatinos);

            // Assert
            assertThat(snapshot.zones()).containsExactly(centro);
        }
    }
}
