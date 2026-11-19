package es.uma.weather.usuarios;

/** Ya existe una cuenta con ese correo. El registro responde con 409. */
public class CorreoYaRegistradoException extends RuntimeException {

    public CorreoYaRegistradoException() {
        super("El correo ya está registrado");
    }

    public CorreoYaRegistradoException(Throwable causa) {
        super("El correo ya está registrado", causa);
    }
}
