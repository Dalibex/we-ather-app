package es.uma.weather.usuarios;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;

/** Cuenta de usuario (tabla {@code usuarios}). */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 254)
    private Correo correo;

    @Column(name = "hash_contrasena", nullable = false)
    private String hashContrasena;

    @Column(name = "privacidad_aceptada_en", nullable = false)
    private Instant privacidadAceptadaEn;

    @CreationTimestamp
    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    /** Para JPA. */
    protected Usuario() {}

    private Usuario(Correo correo, String hashContrasena, Instant privacidadAceptadaEn) {
        this.correo = correo;
        this.hashContrasena = hashContrasena;
        this.privacidadAceptadaEn = privacidadAceptadaEn;
    }

    /**
     * Una cuenta nueva, todavía sin guardar. Valida los datos aquí y no en el constructor, para que
     * nunca quede un objeto a medio crear.
     *
     * @param correo correo de la cuenta, ya normalizado por {@link Correo}
     * @param hashContrasena hash de la contraseña, nunca la contraseña en claro
     * @param privacidadAceptadaEn cuándo aceptó la política de privacidad
     */
    public static Usuario crear(
            Correo correo, String hashContrasena, Instant privacidadAceptadaEn) {
        Objects.requireNonNull(correo, "El correo es obligatorio");
        Objects.requireNonNull(hashContrasena, "El hash de la contraseña es obligatorio");
        if (hashContrasena.isBlank()) {
            throw new IllegalArgumentException("El hash de la contraseña no puede estar vacío");
        }
        Objects.requireNonNull(
                privacidadAceptadaEn, "La aceptación de la privacidad es obligatoria");
        return new Usuario(correo, hashContrasena, privacidadAceptadaEn);
    }

    public UUID getId() {
        return id;
    }

    public Correo getCorreo() {
        return correo;
    }

    public String getHashContrasena() {
        return hashContrasena;
    }

    public Instant getPrivacidadAceptadaEn() {
        return privacidadAceptadaEn;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }
}
