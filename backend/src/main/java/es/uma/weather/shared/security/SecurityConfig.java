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
                // Manual ingestion requires an authenticated user. Restricting it to admins will be
                // addressed with HU-02; browser-form CSRF protection remains disabled for this API.
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/ingestion/**"))
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
