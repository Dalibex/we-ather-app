package es.uma.weather.heatmap.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.io.ClassPathResource;

/** La configuración de application.yaml basta para que la ingesta trabaje sobre Málaga. */
class HeatIngestionConfigurationTest {

    private HeatIngestionProperties properties;

    @BeforeEach
    void bindApplicationYaml() throws IOException {
        var sources =
                new YamlPropertySourceLoader()
                        .load("application", new ClassPathResource("application.yaml"));
        properties =
                new Binder(ConfigurationPropertySources.from(sources))
                        .bind("weather.heat", HeatIngestionProperties.class)
                        .get();
    }

    @Test
    void apuntaALasApisPublicasDeOpenMeteoYOverpass() {
        assertThat(properties.openMeteoUrl()).isEqualTo("https://api.open-meteo.com/v1/forecast");
        assertThat(properties.overpassUrl()).isEqualTo("https://overpass-api.de/api/interpreter");
    }

    @Test
    void todosLosPuntosEstanDentroDelRecuadroDeMalaga() {
        // Overpass bbox order: south, west, north, east.
        double[] bbox =
                Arrays.stream(properties.overpassBbox().split(","))
                        .mapToDouble(Double::parseDouble)
                        .toArray();

        assertThat(bbox).hasSize(4);
        assertThat(properties.points()).isNotEmpty();
        assertThat(properties.points())
                .allSatisfy(
                        point -> {
                            assertThat(point.name()).isNotBlank();
                            assertThat(point.latitude()).isBetween(bbox[0], bbox[2]);
                            assertThat(point.longitude()).isBetween(bbox[1], bbox[3]);
                        });
    }
}
