package es.uma.weather.ingestion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/** El tiempo se refresca solo, con la frecuencia de weather.heat.refresco-cron (T-20.1). */
@SpringJUnitConfig(RefrescoMeteorologicoTest.Configuracion.class)
@TestPropertySource(properties = "weather.heat.refresco-cron=0 0 * * * *")
class RefrescoMeteorologicoTest {

    @Autowired private ApplicationContext context;
    @Autowired private WeatherIngestionService weatherIngestionService;

    @Test
    void programaLaIngestaMeteorologicaConLaFrecuenciaConfigurada() {
        List<CronTask> tareas = tareasCron();

        assertThat(tareas)
                .singleElement()
                .extracting(CronTask::getExpression)
                .isEqualTo("0 0 * * * *");
    }

    @Test
    void cadaRefrescoLanzaLaIngestaMeteorologica() {
        tareasCron().getFirst().getRunnable().run();

        verify(weatherIngestionService).ingest();
    }

    private List<CronTask> tareasCron() {
        return context.getBeansOfType(ScheduledTaskHolder.class).values().stream()
                .map(ScheduledTaskHolder::getScheduledTasks)
                .flatMap(Set::stream)
                .map(ScheduledTask::getTask)
                .filter(CronTask.class::isInstance)
                .map(CronTask.class::cast)
                .toList();
    }

    @Configuration
    @EnableScheduling
    @Import(RefrescoMeteorologico.class)
    static class Configuracion {

        @Bean
        WeatherIngestionService weatherIngestionService() {
            return mock(WeatherIngestionService.class);
        }
    }
}
