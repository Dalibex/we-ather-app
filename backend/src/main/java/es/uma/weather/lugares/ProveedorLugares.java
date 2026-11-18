package es.uma.weather.lugares;

import java.io.IOException;
import java.util.List;

/** Servicio externo que convierte un texto en los lugares que coinciden con él. */
@FunctionalInterface
interface ProveedorLugares {

    /**
     * Busca los lugares que coinciden con el texto.
     *
     * @return los lugares encontrados, del más al menos relevante; vacía si no hay ninguno
     * @throws IOException si el proveedor no responde o no devuelve una lista de lugares. El
     *     mensaje no incluye el texto buscado, para que no acabe en los logs
     */
    List<Lugar> buscar(String texto) throws IOException;
}
