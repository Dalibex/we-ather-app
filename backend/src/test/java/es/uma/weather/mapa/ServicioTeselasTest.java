package es.uma.weather.mapa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ServicioTeselasTest {

    private static final Tesela TESELA = new Tesela(13, 3995, 3196);
    private static final Duration CADUCIDAD = Duration.ofDays(7);
    private static final Instant AHORA = Instant.parse("2026-01-10T12:00:00Z");

    @TempDir Path directorio;

    private final AtomicInteger descargas = new AtomicInteger();

    private ServicioTeselas servicio(ProveedorTeselas proveedor, Instant ahora) {
        Clock reloj = Clock.fixed(ahora, ZoneOffset.UTC);
        return new ServicioTeselas(
                proveedor, new CacheTeselas(directorio, reloj), CADUCIDAD, reloj);
    }

    private ProveedorTeselas proveedorQueDevuelve(byte... imagen) {
        return tesela -> {
            descargas.incrementAndGet();
            return imagen;
        };
    }

    private static ProveedorTeselas proveedorCaido() {
        return tesela -> {
            throw new IOException("OSM no responde");
        };
    }

    @Test
    void laPrimeraVezDescargaLaTeselaDelProveedor() {
        assertThat(servicio(proveedorQueDevuelve((byte) 1), AHORA).obtener(TESELA))
                .containsExactly(1);
        assertThat(descargas).hasValue(1);
    }

    @Test
    void laSegundaVezLaSirveDeLaCacheSinLlamarAlProveedor() {
        ServicioTeselas servicio = servicio(proveedorQueDevuelve((byte) 1), AHORA);

        servicio.obtener(TESELA);
        servicio.obtener(TESELA);

        assertThat(descargas).hasValue(1);
    }

    @Test
    void unaTeselaCaducadaSeVuelveADescargar() {
        servicio(proveedorQueDevuelve((byte) 1), AHORA).obtener(TESELA);

        Instant ochoDiasDespues = AHORA.plus(Duration.ofDays(8));
        assertThat(servicio(proveedorQueDevuelve((byte) 2), ochoDiasDespues).obtener(TESELA))
                .containsExactly(2);
        assertThat(descargas).hasValue(2);
    }

    @Test
    void siElProveedorFallaSirveElUltimoDatoValidoAunqueEsteCaducado() {
        servicio(proveedorQueDevuelve((byte) 1), AHORA).obtener(TESELA);

        Instant ochoDiasDespues = AHORA.plus(Duration.ofDays(8));
        assertThat(servicio(proveedorCaido(), ochoDiasDespues).obtener(TESELA)).containsExactly(1);
    }

    @Test
    void siElProveedorFallaYNoHayNadaGuardadoLaTeselaNoEstaDisponible() {
        assertThatThrownBy(() -> servicio(proveedorCaido(), AHORA).obtener(TESELA))
                .isInstanceOf(TeselaNoDisponibleException.class);
    }
}
