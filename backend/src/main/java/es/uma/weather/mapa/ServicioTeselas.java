package es.uma.weather.mapa;

import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Sirve las teselas del mapa base desde la caché y solo llama al proveedor cuando una tesela falta
 * o ha caducado. Si el proveedor falla, se sirve el último dato válido aunque esté caducado.
 */
@Service
class ServicioTeselas {

    private static final Logger LOG = LoggerFactory.getLogger(ServicioTeselas.class);

    private final ProveedorTeselas proveedor;
    private final CacheTeselas cache;
    private final Duration caducidad;
    private final Clock reloj;

    @Autowired
    ServicioTeselas(
            ProveedorTeselas proveedor,
            CacheTeselas cache,
            @Value("${weather.teselas.caducidad}") Duration caducidad) {
        this(proveedor, cache, caducidad, Clock.systemUTC());
    }

    ServicioTeselas(
            ProveedorTeselas proveedor, CacheTeselas cache, Duration caducidad, Clock reloj) {
        this.proveedor = proveedor;
        this.cache = cache;
        this.caducidad = caducidad;
        this.reloj = reloj;
    }

    byte[] obtener(Tesela tesela) {
        if (estaVigente(tesela)) {
            Optional<byte[]> guardada = cache.leer(tesela);
            if (guardada.isPresent()) {
                return guardada.get();
            }
        }
        try {
            byte[] imagen = proveedor.descargar(tesela);
            cache.guardar(tesela, imagen);
            return imagen;
        } catch (IOException e) {
            LOG.warn("No se pudo descargar la tesela {}: {}", tesela, e.getMessage());
            return cache.leer(tesela).orElseThrow(() -> new TeselaNoDisponibleException(tesela, e));
        }
    }

    private boolean estaVigente(Tesela tesela) {
        Instant limite = reloj.instant().minus(caducidad);
        return cache.guardadaEn(tesela).filter(guardada -> guardada.isAfter(limite)).isPresent();
    }
}
