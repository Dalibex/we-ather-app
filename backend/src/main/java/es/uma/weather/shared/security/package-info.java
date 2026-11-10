/**
 * Configuración de Spring Security: la cadena de filtros que se aplica a cada petición.
 *
 * <p>Por ahora solo {@code /actuator/health/**} es público; el resto de rutas pide HTTP Basic. La
 * autenticación de los usuarios (HU-01 y HU-02) la sustituirá cuando se implemente.
 */
package es.uma.weather.shared.security;
