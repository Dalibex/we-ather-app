package es.uma.weather.lugares;

import es.uma.weather.shared.proveedores.LimitadorPeticiones;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Busca lugares por texto. Guarda en memoria las últimas búsquedas, por su texto normalizado, para
 * no repetir la misma consulta a Nominatim, y solo le pregunta si cabe en su límite de peticiones.
 *
 * <p>El texto buscado nunca se registra en los logs: puede revelar la ruta que pide el usuario
 * (docs/privacidad.md).
 */
@Service
class ServicioLugares {

    static final String PROVEEDOR = "nominatim";

    /** Búsquedas que se guardan; al llegar al máximo se descarta la usada hace más tiempo. */
    private static final int MAX_BUSQUEDAS_GUARDADAS = 1000;

    private static final Logger LOG = LoggerFactory.getLogger(ServicioLugares.class);

    private final ProveedorLugares proveedor;
    private final LimitadorPeticiones limitador;
    private final Map<String, List<Lugar>> cache =
            Collections.synchronizedMap(
                    new LinkedHashMap<>(16, 0.75f, true) {
                        private static final long serialVersionUID = 1L;

                        @Override
                        protected boolean removeEldestEntry(Map.Entry<String, List<Lugar>> e) {
                            return size() > MAX_BUSQUEDAS_GUARDADAS;
                        }
                    });

    ServicioLugares(ProveedorLugares proveedor, LimitadorPeticiones limitador) {
        this.proveedor = proveedor;
        this.limitador = limitador;
    }

    /**
     * @throws BusquedaNoDisponibleException si la búsqueda no está guardada y no se puede preguntar
     *     a Nominatim
     */
    List<Lugar> buscar(String texto) {
        String clave = normalizar(texto);
        List<Lugar> guardada = cache.get(clave);
        if (guardada != null) {
            return guardada;
        }
        if (!limitador.permitir(PROVEEDOR)) {
            throw new BusquedaNoDisponibleException("Límite de peticiones a Nominatim alcanzado");
        }
        List<Lugar> lugares;
        try {
            lugares = List.copyOf(proveedor.buscar(clave));
        } catch (IOException e) {
            LOG.warn("No se pudo buscar en Nominatim: {}", e.getMessage());
            throw new BusquedaNoDisponibleException("Nominatim no está disponible", e);
        }
        cache.put(clave, lugares);
        return lugares;
    }

    /**
     * Sin espacios de más y en minúsculas: Nominatim no distingue entre mayúsculas y minúsculas.
     */
    private static String normalizar(String texto) {
        return texto.strip().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
}
