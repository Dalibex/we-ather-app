package es.uma.weather.mapa;

/** El proveedor no ha devuelto la tesela y tampoco hay una copia guardada que servir. */
class TeselaNoDisponibleException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    TeselaNoDisponibleException(Tesela tesela, Throwable causa) {
        super("No hay tesela " + tesela + " que servir", causa);
    }
}
