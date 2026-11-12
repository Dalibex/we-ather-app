package es.uma.weather.heatmap.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class HeatIngestionPropertiesTest {

    @Test
    void copiesConfiguredPointsToPreventExternalMutation() {
        List<HeatIngestionProperties.GridPoint> points = new ArrayList<>();
        points.add(new HeatIngestionProperties.GridPoint("Centro", 36.72, -4.42));

        HeatIngestionProperties properties =
                new HeatIngestionProperties(
                        "https://weather.test", "https://overpass.test", "1,2,3,4", points);

        points.clear();

        assertEquals(1, properties.points().size());
        assertEquals("Centro", properties.points().getFirst().name());
    }

    @Test
    void usesEmptyListWhenPointsAreNull() {
        HeatIngestionProperties properties =
                new HeatIngestionProperties(
                        "https://weather.test", "https://overpass.test", "1,2,3,4", null);

        assertEquals(List.of(), properties.points());
        assertThrows(
                UnsupportedOperationException.class,
                () ->
                        properties
                                .points()
                                .add(new HeatIngestionProperties.GridPoint("Norte", 36.8, -4.4)));
    }
}
