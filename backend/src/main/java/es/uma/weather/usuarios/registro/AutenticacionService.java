package es.uma.weather.usuarios.registro;

import java.util.UUID;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AutenticacionService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AutenticacionService(
            AuthenticationManager authenticationManager, JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Comprueba el correo y la contraseña y devuelve un token para esa cuenta.
     *
     * @throws org.springframework.security.core.AuthenticationException si no coinciden con ninguna
     *     cuenta
     */
    public String autenticar(String correo, String contrasena) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(correo, contrasena));

        // UsuarioService usa el id de la cuenta como nombre del usuario autenticado.
        return jwtService.generateToken(UUID.fromString(authentication.getName()));
    }
}
