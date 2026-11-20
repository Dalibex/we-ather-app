package es.uma.weather.usuarios.registro;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private static final String SECRETO = base64(new byte[32]);

    private final JwtService servicio = new JwtService(SECRETO, 60_000);

    @Test
    void elTokenGeneradoDevuelveElIdDelUsuario() {
        UUID id = UUID.randomUUID();

        String token = servicio.generateToken(id);

        assertThat(servicio.extractIdUsuario(token)).contains(id);
    }

    // Cualquiera que tenga el token puede leer su contenido: no debe llevar el correo.
    @Test
    void elSujetoDelTokenEsElIdDelUsuario() {
        UUID id = UUID.randomUUID();

        String token = servicio.generateToken(id);

        assertThat(sujeto(token)).isEqualTo(id.toString());
    }

    @Test
    void unTokenManipuladoNoDevuelveId() {
        assertThat(servicio.extractIdUsuario("token.invalido.manipulado")).isEmpty();
    }

    // Los tokens emitidos antes de T-02.3 llevaban el correo como sujeto.
    @Test
    void unTokenFirmadoCuyoSujetoNoEsUnIdNoDevuelveId() {
        String token =
                Jwts.builder()
                        .subject("usuario@example.com")
                        .expiration(new Date(System.currentTimeMillis() + 60_000))
                        .signWith(clave())
                        .compact();

        assertThat(servicio.extractIdUsuario(token)).isEmpty();
    }

    @Test
    void unTokenFirmadoSinSujetoNoDevuelveId() {
        String token =
                Jwts.builder()
                        .expiration(new Date(System.currentTimeMillis() + 60_000))
                        .signWith(clave())
                        .compact();

        assertThat(servicio.extractIdUsuario(token)).isEmpty();
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

    private static SecretKey clave() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRETO));
    }

    private static String sujeto(String token) {
        return Jwts.parser()
                .verifyWith(clave())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    private static String base64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
