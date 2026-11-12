package es.uma.weather.usuarios;

import java.util.Locale;
import java.util.Objects;

/**
 * Correo electrónico de una cuenta, normalizado: sin espacios alrededor y en minúsculas. Así, con
 * espacios o mayúsculas, {@code Usuario@Example.com} es el mismo correo que {@code
 * usuario@example.com} al registrarse y al iniciar sesión.
 *
 * <p>Solo normaliza; validar el formato es cosa del registro (HU-01).
 */
public record Correo(String valor) {

    public Correo {
        Objects.requireNonNull(valor, "El correo es obligatorio");
        valor = valor.strip().toLowerCase(Locale.ROOT);
        if (valor.isEmpty()) {
            throw new IllegalArgumentException("El correo no puede estar vacío");
        }
    }
}
