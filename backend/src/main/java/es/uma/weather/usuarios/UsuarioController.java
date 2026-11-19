package es.uma.weather.usuarios;

import es.uma.weather.usuarios.registro.RegistroRequest;
import es.uma.weather.usuarios.registro.RegistroResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<RegistroResponse> registrar(@RequestBody RegistroRequest request) {

        if (request.correo() == null) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }

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
}
