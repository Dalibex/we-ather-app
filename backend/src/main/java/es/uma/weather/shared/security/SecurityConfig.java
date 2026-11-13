package es.uma.weather.shared.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers("/actuator/health/**", "/api/v1/teselas/**")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                // Manual ingestion is triggered by scripts or admins, not by browser forms, so
                // CSRF protection does not apply there. The rest of the API keeps the default.
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/ingestion/**"))
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
