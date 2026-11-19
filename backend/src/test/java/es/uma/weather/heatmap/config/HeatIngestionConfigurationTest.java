package es.uma.weather.heatmap.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.support.CronExpression;

/** La configuración de application.yaml basta para que la ingesta trabaje sobre Málaga. */
class HeatIngestionConfigurationTest {

    private Binder binder;
    private HeatIngestionProperties properties;

    @BeforeEach
    void bindApplicationYaml() throws IOException {
        var sources =
                new YamlPropertySourceLoader()
                        .load("application", new ClassPathResource("application.yaml"));
        binder =
                new Binder(
                        ConfigurationPropertySources.from(sources),
                        new PropertySourcesPlaceholdersResolver(sources));
        properties = binder.bind("weather.heat", HeatIngestionProperties.class).get();
    }

    @Test
    void apuntaALasApisPublicasDeOpenMeteoYOverpass() {
        assertThat(properties.openMeteoUrl()).isEqualTo("https://api.open-meteo.com/v1/forecast");
        assertThat(properties.overpassUrl()).isEqualTo("https://overpass-api.de/api/interpreter");
    }

    @Test
    void seIdentificaAnteLosProveedoresConElUserAgentDeWeather() {
        assertThat(properties.userAgent()).startsWith("WEather/");
    }

    @Test
    void limitaLaEsperaDeLasPeticionesALosProveedores() {
        // Las ingestas se lanzarán solas (T-17.4): una petición colgada no puede bloquearlas.
        assertThat(binder.bind("spring.http.clients.connect-timeout", Duration.class).get())
                .isPositive();
        assertThat(binder.bind("spring.http.clients.read-timeout", Duration.class).get())
                .isPositive();
    }

    @Test
    void refrescaElTiempoCadaHoraEnPunto() {
        // HU-20: los datos meteorológicos se consultan cada hora (T-20.1).
        CronExpression cron =
                CronExpression.parse(binder.bind("weather.heat.refresco-cron", String.class).get());
        LocalDateTime diezYMedia = LocalDateTime.of(2026, 7, 20, 10, 30);

        assertThat(cron.next(diezYMedia)).isEqualTo(LocalDateTime.of(2026, 7, 20, 11, 0));
        assertThat(cron.next(LocalDateTime.of(2026, 7, 20, 11, 0)))
                .isEqualTo(LocalDateTime.of(2026, 7, 20, 12, 0));
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
