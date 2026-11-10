package es.uma.weather.usuarios;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acceso a las cuentas de usuario. Se busca siempre por {@link Correo}, que ya va normalizado. */
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    /** La cuenta con ese correo, para el inicio de sesión (HU-02). */
    Optional<Usuario> findByCorreo(Correo correo);

    /** Si el correo ya está registrado, para el registro (HU-01). */
    boolean existsByCorreo(Correo correo);
}
