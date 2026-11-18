package es.uma.weather.lugares;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Búsqueda de lugares por texto para elegir el origen y el destino. Es pública, porque las rutas se
 * buscan sin cuenta. La app la llama al confirmar el texto, no mientras se escribe, como pide la
 * política de uso de Nominatim.
 */
@RestController
class LugaresController {

    /** Más que suficiente para una dirección; evita reenviar a Nominatim textos sin sentido. */
    private static final int LONGITUD_MAXIMA = 200;

    private final ServicioLugares servicio;

    LugaresController(ServicioLugares servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/api/lugares")
    ResponseEntity<List<Lugar>> buscar(@RequestParam("q") String texto) {
        if (texto.isBlank() || texto.length() > LONGITUD_MAXIMA) {
            return ResponseEntity.badRequest().build();
        }
        try {
            return ResponseEntity.ok(servicio.buscar(texto));
        } catch (BusquedaNoDisponibleException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }
}
