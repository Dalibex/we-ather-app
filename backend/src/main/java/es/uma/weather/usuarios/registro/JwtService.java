package es.uma.weather.usuarios.registro;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public final class JwtService {

    private final SecretKey signingKey;
    private final long expiration;

    /**
     * Si el secreto no está en Base64 o tiene menos de 256 bits, lanza una {@link JwtException}: la
     * aplicación no arranca con una clave de firma inválida.
     */
    public JwtService(
            @Value("${jwt.secret}") String secretKey, @Value("${jwt.expiration}") long expiration) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.expiration = expiration;
    }

    /**
     * Token firmado para el usuario con ese id. El sujeto es el id y no el correo: cualquiera que
     * tenga el token puede leer su contenido (minimización, RGPD).
     */
    public String generateToken(UUID idUsuario) {

        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expiration);

        return Jwts.builder()
                .subject(idUsuario.toString())
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(signingKey)
                .compact();
    }

    /**
     * El id del usuario (sujeto) del token, o vacío si el token no es válido: mal formado, con la
     * firma falsificada, caducado, sin sujeto o con un sujeto que no es un id.
     */
    public Optional<UUID> extractIdUsuario(String token) {
        try {
            Claims claims =
                    Jwts.parser()
                            .verifyWith(signingKey)
                            .build()
                            .parseSignedClaims(token)
                            .getPayload();
            String sujeto = claims.getSubject();
            return sujeto == null ? Optional.empty() : Optional.of(UUID.fromString(sujeto));
        } catch (JwtException | IllegalArgumentException e) {
            // UUID.fromString lanza IllegalArgumentException si el sujeto no es un id.
            return Optional.empty();
        }
    }
}
