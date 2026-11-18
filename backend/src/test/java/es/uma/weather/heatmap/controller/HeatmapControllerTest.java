package es.uma.weather.heatmap.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;

class HeatmapControllerTest {

    private final JdbcTemplate jdbcTemplate = org.mockito.Mockito.mock(JdbcTemplate.class);
    private final HeatmapController controller = new HeatmapController(jdbcTemplate);

    @Test
    void rejectsSouthLatitudeGreaterThanNorthLatitude() {
        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> controller.getLatestHeatmap(36.8, 36.7, -4.5, -4.4));

        assertEquals(400, exception.getStatusCode().value());
        verifyNoInteractions(jdbcTemplate);
    }

    @Test
    void rejectsWestLongitudeGreaterThanEastLongitude() {
        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> controller.getLatestHeatmap(36.7, 36.8, -4.4, -4.5));

        assertEquals(400, exception.getStatusCode().value());
        verifyNoInteractions(jdbcTemplate);
    }

    @Test
    void rejectsCoordinatesOutsideGeographicLimits() {
        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> controller.getLatestHeatmap(-91, 36.8, -4.5, -4.4));

        assertEquals(400, exception.getStatusCode().value());
        verifyNoInteractions(jdbcTemplate);
    }
}
