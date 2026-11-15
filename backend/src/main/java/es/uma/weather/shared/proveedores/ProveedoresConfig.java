package es.uma.weather.shared.proveedores;

import java.time.Clock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LimitesProveedoresProperties.class)
class ProveedoresConfig {

    @Bean
    LimitadorPeticiones limitadorPeticiones(LimitesProveedoresProperties properties) {
        return new LimitadorPeticiones(properties.limites(), Clock.systemUTC());
    }
}
