package es.uma.weather.heatmap.scheduling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import es.uma.weather.heatmap.service.HeatmapGenerator;
import es.uma.weather.ingestion.service.UrbanDataIngestionService;
import java.io.IOException;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.scheduling.support.CronTrigger;

@DisplayName("Planificación del mapa de calor: qué crons registra Spring a partir de la propiedad")
class HeatmapSchedulingConfigurationTest {

    private static final String GENERATION_CRON = "0 15 * * * *";
    private static final String URBAN_CRON = "0 0 4 * * MON";
    private static final ZoneId MADRID = ZoneId.of("Europe/Madrid");

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withUserConfiguration(SchedulingConfig.class, HeatmapScheduler.class)
                    .withBean(HeatmapGenerator.class, () -> mock(HeatmapGenerator.class))
                    .withBean(
                            UrbanDataIngestionService.class,
                            () -> mock(UrbanDataIngestionService.class))
                    .withPropertyValues(
                            "weather.heat.schedule.generation=" + GENERATION_CRON,
                            "weather.heat.schedule.urban=" + URBAN_CRON);

    private static List<CronTask> cronTasks(ScheduledTaskHolder holder) {
        return holder.getScheduledTasks().stream()
                .map(ScheduledTask::getTask)
                .filter(CronTask.class::isInstance)
                .map(CronTask.class::cast)
                .toList();
    }

    @Test
    @DisplayName("registra dos tareas cron con las expresiones de generación y de calles")
    void context_schedulingEnabled_registersTheTwoConfiguredCrons() {
        contextRunner.run(
                context -> {
                    // Arrange
                    ScheduledTaskHolder holder = context.getBean(ScheduledTaskHolder.class);

                    // Act
                    List<String> expressions =
                            cronTasks(holder).stream().map(CronTask::getExpression).toList();

                    // Assert
                    assertThat(expressions).containsExactlyInAnyOrder(GENERATION_CRON, URBAN_CRON);
                });
    }

    @Test
    @DisplayName("ambas tareas cron se evalúan en la zona Europe/Madrid")
    void context_schedulingEnabled_cronsRunInMadridZone() {
        contextRunner.run(
                context -> {
                    // Arrange: CronTrigger no expone la zona, pero su equals la compara
                    ScheduledTaskHolder holder = context.getBean(ScheduledTaskHolder.class);

                    // Act
                    List<Object> triggers =
                            cronTasks(holder).stream()
                                    .map(task -> (Object) task.getTrigger())
                                    .toList();

                    // Assert
                    assertThat(triggers)
                            .containsExactlyInAnyOrder(
                                    new CronTrigger(GENERATION_CRON, MADRID),
                                    new CronTrigger(URBAN_CRON, MADRID));
                });
    }

    @Test
    @DisplayName("application.yaml define las expresiones por defecto de ambas tareas")
    void applicationYaml_scheduleDefaults_areTheAgreedCrons() throws IOException {
        // Arrange
        var sources =
                new YamlPropertySourceLoader()
                        .load("application", new ClassPathResource("application.yaml"));
        Binder binder = new Binder(ConfigurationPropertySources.from(sources));

        // Act
        var schedule = binder.bind("weather.heat.schedule", Map.class).get();

        // Assert
        assertThat(schedule)
                .containsEntry("generation", GENERATION_CRON)
                .containsEntry("urban", URBAN_CRON);
    }
}
