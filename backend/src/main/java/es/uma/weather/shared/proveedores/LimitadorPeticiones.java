package es.uma.weather.shared.proveedores;

import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Decide si se puede hacer una petición más a un proveedor externo sin superar su límite.
 *
 * <p>Cuenta las peticiones en ventanas fijas: al terminar la ventana el contador vuelve a cero.
 * Quien llama al proveedor pregunta antes con {@link #permitir(String)}; si la respuesta es {@code
 * false}, no llama y usa el último dato válido que tenga guardado.
 */
public class LimitadorPeticiones {

    private final Map<String, Limite> limites;
    private final Clock reloj;
    private final Map<String, Ventana> ventanas = new HashMap<>();

    public LimitadorPeticiones(Map<String, Limite> limites, Clock reloj) {
        this.limites = Map.copyOf(limites);
        this.reloj = reloj;
    }

    /**
     * Registra una petición al proveedor si cabe en su límite.
     *
     * @param proveedor nombre del proveedor, tal como aparece en la configuración
     * @return {@code true} si se puede hacer la petición; {@code false} si se ha alcanzado el
     *     límite
     * @throws IllegalArgumentException si el proveedor no tiene límite configurado
     */
    public synchronized boolean permitir(String proveedor) {
        Limite limite = limites.get(proveedor);
        if (limite == null) {
            throw new IllegalArgumentException(
                    "El proveedor " + proveedor + " no tiene límite de peticiones configurado");
        }
        Instant ahora = reloj.instant();
        Ventana ventana = ventanas.get(proveedor);
        if (ventana == null || !ahora.isBefore(ventana.inicio().plus(limite.ventana()))) {
            ventana = new Ventana(ahora, 0);
        }
        if (ventana.peticiones() >= limite.maxPeticiones()) {
            return false;
        }
        ventanas.put(proveedor, new Ventana(ventana.inicio(), ventana.peticiones() + 1));
        return true;
    }

    private record Ventana(Instant inicio, int peticiones) {}
}
