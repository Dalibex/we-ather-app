package es.uma.weather.mapa;

import java.time.Duration;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Teselas del mapa base para la app móvil. Es público, porque el mapa se ve sin iniciar sesión, y
 * solo sirve teselas que cubren Málaga, para que no se pueda usar como pasarela a OpenStreetMap.
 */
@RestController
class TeselasController {

    /** Tiempo que la app puede reutilizar una tesela sin volver a pedirla. */
    private static final Duration VALIDEZ_EN_EL_CLIENTE = Duration.ofDays(7);

    private final ServicioTeselas servicio;

    TeselasController(ServicioTeselas servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/api/v1/teselas/{z}/{x}/{y}.png")
    ResponseEntity<byte[]> tesela(@PathVariable int z, @PathVariable int x, @PathVariable int y) {
        Tesela tesela;
        try {
            tesela = new Tesela(z, x, y);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
        if (!tesela.cortaMalaga()) {
            return ResponseEntity.notFound().build();
        }
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .cacheControl(CacheControl.maxAge(VALIDEZ_EN_EL_CLIENTE).cachePublic())
                    .body(servicio.obtener(tesela));
        } catch (TeselaNoDisponibleException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }
}
