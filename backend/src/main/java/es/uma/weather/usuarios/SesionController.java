package es.uma.weather.usuarios;

import es.uma.weather.usuarios.registro.AutenticacionService;
import es.uma.weather.usuarios.registro.InicioSesionRequest;
import es.uma.weather.usuarios.registro.TokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Inicio de sesión (HU-02): crea una sesión a partir del correo y la contraseña y devuelve el token
 * con el que la app se autentica. La sesión no se guarda en el servidor: cerrar sesión es que la
 * app deje de usar el token.
 */
@RestController
@RequestMapping("/api/v1/sesiones")
public class SesionController {

    private static final String TIPO_TOKEN = "Bearer";

    private final AutenticacionService autenticacionService;

    public SesionController(AutenticacionService autenticacionService) {
        this.autenticacionService = autenticacionService;
    }

    @PostMapping
    public ResponseEntity<TokenResponse> iniciarSesion(@RequestBody InicioSesionRequest request) {

        if (request.correo() == null || request.correo().isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }

        if (request.contrasena() == null || request.contrasena().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }

        String token = autenticacionService.autenticar(request.correo(), request.contrasena());

        return ResponseEntity.ok(new TokenResponse(token, TIPO_TOKEN));
    }
}
