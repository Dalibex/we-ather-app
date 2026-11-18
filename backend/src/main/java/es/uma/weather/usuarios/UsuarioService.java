package es.uma.weather.usuarios;

import java.time.Instant;
import java.util.Collections;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository repositorioUsuario;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repositorioUsuario, PasswordEncoder passwordEncoder) {
        this.repositorioUsuario = repositorioUsuario;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {

        Correo correoUsuario = new Correo(correo);

        Usuario usuario =
                repositorioUsuario
                        .findByCorreo(correoUsuario)
                        .orElseThrow(
                                () ->
                                        new UsernameNotFoundException(
                                                "No existe ningún usuario con ese correo"));

        return new User(
                usuario.getCorreo().valor(), usuario.getHashContrasena(), Collections.emptyList());
    }

    public Usuario registerUser(Correo correo, String contrasena, boolean aceptaPrivacidad) {

        if (!aceptaPrivacidad) {
            throw new IllegalArgumentException("Debes aceptar la política de privacidad");
        }

        if (contrasena == null || contrasena.isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }

        if (repositorioUsuario.existsByCorreo(correo)) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        String hash = passwordEncoder.encode(contrasena);

        Usuario nuevoUsuario = Usuario.crear(correo, hash, Instant.now());

        return repositorioUsuario.save(nuevoUsuario);
    }
}
