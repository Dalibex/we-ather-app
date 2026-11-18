package es.uma.weather.usuarios.registro;

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

    public String autenticar(String correo, String contrasena) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(correo, contrasena));

        return jwtService.generateToken(authentication.getName());
    }
}
