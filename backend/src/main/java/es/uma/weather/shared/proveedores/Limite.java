package es.uma.weather.shared.proveedores;

import java.time.Duration;

/**
 * Máximo de peticiones que se hacen a un proveedor dentro de una ventana de tiempo.
 *
 * @param maxPeticiones peticiones permitidas en cada ventana; mayor que cero
 * @param ventana duración de la ventana; mayor que cero
 */
public record Limite(int maxPeticiones, Duration ventana) {

    public Limite {
        if (maxPeticiones <= 0) {
            throw new IllegalArgumentException("maxPeticiones debe ser mayor que cero");
        }
        if (ventana == null || ventana.isZero() || ventana.isNegative()) {
            throw new IllegalArgumentException("La ventana debe ser mayor que cero");
        }
    }
}
