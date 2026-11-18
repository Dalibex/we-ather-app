package es.uma.weather.lugares;

/**
 * No se puede preguntar a Nominatim, porque se ha alcanzado su límite de peticiones o porque no
 * responde, y la búsqueda no estaba guardada.
 */
class BusquedaNoDisponibleException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    BusquedaNoDisponibleException(String mensaje) {
        super(mensaje);
    }

    BusquedaNoDisponibleException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
