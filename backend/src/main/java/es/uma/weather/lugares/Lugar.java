package es.uma.weather.lugares;

/**
 * Un lugar que coincide con el texto buscado.
 *
 * @param nombre nombre completo del lugar, tal como lo devuelve Nominatim
 * @param lat latitud en grados (WGS 84)
 * @param lon longitud en grados (WGS 84)
 */
public record Lugar(String nombre, double lat, double lon) {}
