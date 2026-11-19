package es.uma.weather.usuarios.registro;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.Optional;
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

    public String generateToken(String correo) {

        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expiration);

        return Jwts.builder()
                .subject(correo)
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(signingKey)
                .compact();
    }

    /**
     * El correo (sujeto) del token, o vacío si el token no es válido: mal formado, con la firma
     * falsificada, caducado o sin sujeto.
     */
    public Optional<String> extractCorreo(String token) {
        try {
            Claims claims =
                    Jwts.parser()
                            .verifyWith(signingKey)
                            .build()
                            .parseSignedClaims(token)
                            .getPayload();
            String sujeto = claims.getSubject();
            return sujeto == null || sujeto.isBlank() ? Optional.empty() : Optional.of(sujeto);
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
