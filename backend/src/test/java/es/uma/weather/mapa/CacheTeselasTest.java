package es.uma.weather.mapa;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CacheTeselasTest {

    private static final Tesela TESELA = new Tesela(13, 3995, 3196);
    private static final Instant AHORA = Instant.parse("2026-01-10T12:00:00Z");

    @TempDir Path directorio;

    @Test
    void unaTeselaSinGuardarNoEsta() {
        CacheTeselas cache = new CacheTeselas(directorio, Clock.systemUTC());

        assertThat(cache.leer(TESELA)).isEmpty();
        assertThat(cache.guardadaEn(TESELA)).isEmpty();
    }

    @Test
    void devuelveLaImagenGuardadaYCuandoSeGuardo() {
        CacheTeselas cache = new CacheTeselas(directorio, Clock.fixed(AHORA, ZoneOffset.UTC));

        cache.guardar(TESELA, new byte[] {1, 2, 3});

        assertThat(cache.leer(TESELA)).contains(new byte[] {1, 2, 3});
        assertThat(cache.guardadaEn(TESELA)).contains(AHORA);
    }

    @Test
    void laTeselaSigueGuardadaTrasReiniciar() {
        new CacheTeselas(directorio, Clock.systemUTC()).guardar(TESELA, new byte[] {7});

        assertThat(new CacheTeselas(directorio, Clock.systemUTC()).leer(TESELA))
                .contains(new byte[] {7});
    }
}
