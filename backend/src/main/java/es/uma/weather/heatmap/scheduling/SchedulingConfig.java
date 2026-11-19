package es.uma.weather.heatmap.scheduling;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Activa las tareas {@code @Scheduled}; la T-20.1 la reutiliza para la ingesta horaria del tiempo.
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {}
