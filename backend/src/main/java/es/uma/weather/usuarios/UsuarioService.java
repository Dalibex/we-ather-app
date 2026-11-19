package es.uma.weather.usuarios;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;
import java.util.regex.Pattern;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService implements UserDetailsService {

    private static final int LONGITUD_MINIMA_CONTRASENA = 8;
    // BCrypt no admite contraseñas de más de 72 bytes (en UTF-8, una tilde ocupa 2 y un emoji 4).
    private static final int MAXIMO_BYTES_CONTRASENA = 72;
    private static final int LONGITUD_MAXIMA_CORREO = 254;
    private static final Pattern FORMATO_CORREO = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final String RESTRICCION_CORREO_UNICO = "usuarios_correo_unico";

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

        if (contrasena.codePointCount(0, contrasena.length()) < LONGITUD_MINIMA_CONTRASENA) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }

        if (contrasena.getBytes(StandardCharsets.UTF_8).length > MAXIMO_BYTES_CONTRASENA) {
            throw new IllegalArgumentException("La contraseña es demasiado larga");
        }

        validarCorreo(correo);

        if (repositorioUsuario.existsByCorreo(correo)) {
            throw new CorreoYaRegistradoException();
        }

        String hash = passwordEncoder.encode(contrasena);

        if (hash == null) {
            throw new IllegalStateException("No se pudo generar el hash de la contraseña");
        }

        Usuario nuevoUsuario = Usuario.crear(correo, hash, Instant.now());

        try {
            repositorioUsuario.saveAndFlush(nuevoUsuario);
        } catch (DataIntegrityViolationException e) {
            if (!esCorreoDuplicado(e)) {
                throw e;
            }
            // Otro registro con el mismo correo se coló entre la comprobación y el guardado.
            throw new CorreoYaRegistradoException(e);
        }

        return nuevoUsuario;
    }

    // Mismas reglas que la validación de la app móvil (validacionRegistro.ts).
    private static void validarCorreo(Correo correo) {
        String valor = correo.valor();

        if (valor.length() > LONGITUD_MAXIMA_CORREO) {
            throw new IllegalArgumentException("El correo no puede superar los 254 caracteres");
        }

        if (!FORMATO_CORREO.matcher(valor).matches()) {
            throw new IllegalArgumentException("El correo no tiene un formato válido");
        }
    }

    private static boolean esCorreoDuplicado(DataIntegrityViolationException e) {
        String mensaje = NestedExceptionUtils.getMostSpecificCause(e).getMessage();
        return mensaje != null && mensaje.contains(RESTRICCION_CORREO_UNICO);
    }
}
