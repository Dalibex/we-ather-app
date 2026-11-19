package es.uma.weather.heatmap.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import es.uma.weather.heatmap.service.HeatmapGenerator;
import es.uma.weather.heatmap.service.ReportCombiner;
import es.uma.weather.heatmap.service.ValidatedReport;
import es.uma.weather.heatmap.service.ZoneHeat;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.ClassPathResource;

@DisplayName(
        "Configuración de ReportCombiner: la propiedad weather.reportes.peso-estimacion llega al bean")
class ReportCombinerConfigurationTest {

    /**
     * Escaneo con los filtros por defecto: solo recoge las clases anotadas como componente. Deja
     * fuera HeatmapGenerator, que necesita la base de datos y no pinta nada en esta prueba.
     */
    @Configuration
    @ComponentScan(
            basePackageClasses = ReportCombiner.class,
            excludeFilters =
                    @ComponentScan.Filter(
                            type = FilterType.ASSIGNABLE_TYPE,
                            classes = HeatmapGenerator.class))
    static class ScanServicePackage {}

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withUserConfiguration(HeatmapConfig.class, ScanServicePackage.class);

    @Test
    @DisplayName("el peso configurado en la propiedad es el que usa el bean al combinar")
    void context_pesoEstimacionConfigured_combinerUsesThatWeight() {
        contextRunner
                .withPropertyValues("weather.reportes.peso-estimacion=2")
                .run(
                        context -> {
                            // Arrange
                            ZoneHeat zone = new ZoneHeat("Centro", 36.7213, -4.4214, 40.0);
                            List<ValidatedReport> reports =
                                    List.of(new ValidatedReport(36.7213, -4.4214, 5));

                            // Act
                            List<ZoneHeat> result =
                                    context.getBean(ReportCombiner.class)
                                            .combine(List.of(zone), reports);

                            // Assert
                            assertThat(context).hasSingleBean(ReportCombiner.class);
                            assertThat(result.get(0).heatScore()).isCloseTo(60.0, within(0.001));
                        });
    }

    @Test
    @DisplayName("sin la propiedad, el contexto no arranca por el peso inválido")
    void context_pesoEstimacionMissing_failsToStart() {
        contextRunner.run(
                context -> {
                    // Arrange: ninguna propiedad weather.reportes, así que el peso se enlaza a 0

                    // Act
                    Throwable failure = context.getStartupFailure();

                    // Assert
                    assertThat(failure).rootCause().isInstanceOf(IllegalArgumentException.class);
                });
    }

    @Test
    @DisplayName("application.yaml fija el peso de la estimación en 5")
    void applicationYaml_pesoEstimacion_isFive() throws IOException {
        // Arrange
        var sources =
                new YamlPropertySourceLoader()
                        .load("application", new ClassPathResource("application.yaml"));
        Binder binder = new Binder(ConfigurationPropertySources.from(sources));

        // Act
        ReportProperties properties = binder.bind("weather.reportes", ReportProperties.class).get();

        // Assert
        assertThat(properties.pesoEstimacion()).isEqualTo(5);
    }
}
