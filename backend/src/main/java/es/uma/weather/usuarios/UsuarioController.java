package es.uma.weather.usuarios;

import es.uma.weather.usuarios.registro.AutenticacionService;
import es.uma.weather.usuarios.registro.RegistroRequest;
import es.uma.weather.usuarios.registro.RegistroResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AutenticacionService autenticacionService;

    public UsuarioController(
            UsuarioService usuarioService, AutenticacionService autenticacionService) {

        this.usuarioService = usuarioService;
        this.autenticacionService = autenticacionService;
    }

    @PostMapping("/registro")
    public ResponseEntity<RegistroResponse> registrar(@RequestBody RegistroRequest request) {

        Correo correo = new Correo(request.correo());

        Usuario usuario =
                usuarioService.registerUser(
                        correo, request.contrasena(), request.aceptaPrivacidad());

        RegistroResponse respuesta =
                new RegistroResponse(
                        usuario.getId(),
                        usuario.getCorreo().valor(),
                        "Usuario registrado correctamente");

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    // @PostMapping("/login") Para implementación del siguiente que venga ;)

}
