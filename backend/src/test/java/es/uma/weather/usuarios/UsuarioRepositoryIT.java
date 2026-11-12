package es.uma.weather.usuarios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import es.uma.weather.TestcontainersConfiguration;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

/** La tabla usuarios, creada por Flyway, sobre PostGIS en Testcontainers. */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class UsuarioRepositoryIT {

    private static final String HASH = "{bcrypt}hash-de-ejemplo";
    private static final Instant ACEPTADA = Instant.parse("2026-11-10T10:00:00Z");

    @Autowired private UsuarioRepository usuarios;

    @Autowired private EntityManager entityManager;

    @Test
    void guardaUnUsuarioYLoRecuperaPorSuCorreo() {
        var guardado =
                usuarios.saveAndFlush(
                        Usuario.crear(new Correo("usuario@example.com"), HASH, ACEPTADA));
        entityManager.clear();

        var leido = usuarios.findByCorreo(new Correo("usuario@example.com")).orElseThrow();

        assertThat(leido.getId()).isEqualTo(guardado.getId());
        assertThat(leido.getCorreo()).isEqualTo(new Correo("usuario@example.com"));
        assertThat(leido.getHashContrasena()).isEqualTo(HASH);
        assertThat(leido.getPrivacidadAceptadaEn()).isEqualTo(ACEPTADA);
    }

    @Test
    void encuentraElCorreoAunqueSeEscribaConMayusculasYEspacios() {
        usuarios.saveAndFlush(Usuario.crear(new Correo("usuario@example.com"), HASH, ACEPTADA));
        entityManager.clear();

        assertThat(usuarios.findByCorreo(new Correo("  Usuario@EXAMPLE.com "))).isPresent();
    }

    @Test
    void sabeSiUnCorreoYaEstaRegistrado() {
        usuarios.saveAndFlush(Usuario.crear(new Correo("usuario@example.com"), HASH, ACEPTADA));

        assertThat(usuarios.existsByCorreo(new Correo("USUARIO@example.com"))).isTrue();
        assertThat(usuarios.existsByCorreo(new Correo("otro@example.com"))).isFalse();
    }

    @Test
    void rechazaDosCuentasConElMismoCorreo() {
        usuarios.saveAndFlush(Usuario.crear(new Correo("usuario@example.com"), HASH, ACEPTADA));

        assertThatThrownBy(
                        () ->
                                usuarios.saveAndFlush(
                                        Usuario.crear(
                                                new Correo("Usuario@example.com"), HASH, ACEPTADA)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void registraCuandoSeCreaLaCuenta() {
        var antes = Instant.now();

        var guardado =
                usuarios.saveAndFlush(
                        Usuario.crear(new Correo("usuario@example.com"), HASH, ACEPTADA));

        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getCreadoEn()).isNotNull().isAfterOrEqualTo(antes.minusSeconds(1));
    }
}
