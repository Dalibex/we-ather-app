package es.uma.weather.shared.security;

import es.uma.weather.usuarios.registro.JwtAuthenticationFilter;
import jakarta.servlet.DispatcherType;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Evita que Spring Boot registre el filtro JWT como filtro del servidor, porque es un
     * {@code @Component}: solo debe ejecutarse dentro de la cadena de Spring Security.
     */
    @Bean
    FilterRegistrationBean<JwtAuthenticationFilter> registroFiltroJwtFueraDeSeguridad(
            JwtAuthenticationFilter jwtAuthenticationFilter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registro =
                new FilterRegistrationBean<>(jwtAuthenticationFilter);
        registro.setEnabled(false);
        return registro;
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

        // La API no usa sesiones ni cookies: se autentica con un JWT en la cabecera
        // Authorization, que el navegador no envía solo, así que CSRF no aplica.
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(
                        exceptions ->
                                exceptions.authenticationEntryPoint(
                                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(
                        auth ->
                                auth.dispatcherTypeMatchers(DispatcherType.ERROR)
                                        .permitAll()
                                        .requestMatchers(
                                                "/actuator/health/**", "/api/v1/teselas/**")
                                        .permitAll()
                                        .requestMatchers(
                                                HttpMethod.POST,
                                                "/api/v1/usuarios",
                                                "/api/v1/sesiones")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                .addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
