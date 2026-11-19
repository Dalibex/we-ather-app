package es.uma.weather.heatmap.config;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HeatIngestionPropertiesTest {

    @Test
    void copiesConfiguredPointsToPreventExternalMutation() {
        List<HeatIngestionProperties.GridPoint> points = new ArrayList<>();
        points.add(new HeatIngestionProperties.GridPoint("Centro", 36.72, -4.42));

        HeatIngestionProperties properties =
                new HeatIngestionProperties(
                        "https://weather.test",
                        "https://overpass.test",
                        "1,2,3,4",
                        "WEather/test",
                        points);

        points.clear();

        assertEquals(1, properties.points().size());
        assertEquals("Centro", properties.points().getFirst().name());
    }

    @Test
    void usesEmptyListWhenPointsAreNull() {
        HeatIngestionProperties properties =
                new HeatIngestionProperties(
                        "https://weather.test",
                        "https://overpass.test",
                        "1,2,3,4",
                        "WEather/test",
                        null);

        assertEquals(List.of(), properties.points());
        assertThrows(
                UnsupportedOperationException.class,
                () ->
                        properties
                                .points()
                                .add(new HeatIngestionProperties.GridPoint("Norte", 36.8, -4.4)));
    }

    @Test
    @DisplayName("rechaza dos puntos con el mismo nombre e indica cuál se repite")
    void constructor_duplicatePointName_throwsIllegalArgumentException() {
        // Arrange
        List<HeatIngestionProperties.GridPoint> points =
                List.of(
                        new HeatIngestionProperties.GridPoint("Centro", 36.72, -4.42),
                        new HeatIngestionProperties.GridPoint("Centro", 36.8, -4.4));

        // Act & Assert
        assertThatThrownBy(
                        () ->
                                new HeatIngestionProperties(
                                        "https://weather.test",
                                        "https://overpass.test",
                                        "1,2,3,4",
                                        "WEather/test",
                                        points))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Centro");
    }

    @Test
    @DisplayName("acepta dos puntos con nombres distintos")
    void constructor_distinctPointNames_keepsBothPoints() {
        // Arrange
        List<HeatIngestionProperties.GridPoint> points =
                List.of(
                        new HeatIngestionProperties.GridPoint("Centro", 36.72, -4.42),
                        new HeatIngestionProperties.GridPoint("Norte", 36.8, -4.4));

        // Act
        HeatIngestionProperties properties =
                new HeatIngestionProperties(
                        "https://weather.test",
                        "https://overpass.test",
                        "1,2,3,4",
                        "WEather/test",
                        points);

        // Assert
        assertEquals(points, properties.points());
    }
}
