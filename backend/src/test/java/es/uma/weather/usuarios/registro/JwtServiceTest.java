package es.uma.weather.usuarios.registro;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private static final String SECRETO = base64(new byte[32]);

    private final JwtService servicio = new JwtService(SECRETO, 60_000);

    @Test
    void elTokenGeneradoDevuelveElCorreo() {
        String token = servicio.generateToken("usuario@example.com");

        assertThat(servicio.extractCorreo(token)).contains("usuario@example.com");
    }

    @Test
    void unTokenManipuladoNoDevuelveCorreo() {
        assertThat(servicio.extractCorreo("token.invalido.manipulado")).isEmpty();
    }

    @Test
    void unTokenFirmadoSinSujetoNoDevuelveCorreo() {
        SecretKey clave = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRETO));
        String token =
                Jwts.builder()
                        .expiration(new Date(System.currentTimeMillis() + 60_000))
                        .signWith(clave)
                        .compact();

        assertThat(servicio.extractCorreo(token)).isEmpty();
    }

    @Test
    void unSecretoQueNoEsBase64HaceFallarLaConstruccion() {
        assertThatThrownBy(() -> new JwtService("esto no es base64 !", 60_000))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void unSecretoDeMenosDe256BitsHaceFallarLaConstruccion() {
        assertThatThrownBy(() -> new JwtService(base64(new byte[16]), 60_000))
                .isInstanceOf(JwtException.class);
    }

    private static String base64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
