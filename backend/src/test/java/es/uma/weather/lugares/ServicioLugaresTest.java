package es.uma.weather.lugares;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import es.uma.weather.shared.proveedores.LimitadorPeticiones;
import es.uma.weather.shared.proveedores.Limite;
import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class ServicioLugaresTest {

    private static final Lugar LARIOS = new Lugar("Calle Larios, Málaga", 36.7196, -4.42);
    private static final Clock RELOJ =
            Clock.fixed(Instant.parse("2026-01-10T12:00:00Z"), ZoneOffset.UTC);

    private final List<String> busquedas = new ArrayList<>();

    private ProveedorLugares proveedorQueDevuelve(Lugar... lugares) {
        return texto -> {
            busquedas.add(texto);
            return List.of(lugares);
        };
    }

    private static LimitadorPeticiones limitador(int maxPeticiones) {
        return new LimitadorPeticiones(
                Map.of("nominatim", new Limite(maxPeticiones, Duration.ofSeconds(1))), RELOJ);
    }

    @Test
    void devuelveLosLugaresDelProveedor() {
        ServicioLugares servicio = new ServicioLugares(proveedorQueDevuelve(LARIOS), limitador(1));

        assertThat(servicio.buscar("calle larios")).containsExactly(LARIOS);
    }

    @Test
    void laMismaBusquedaDosVecesSoloLlamaUnaVezAlProveedor() {
        ServicioLugares servicio = new ServicioLugares(proveedorQueDevuelve(LARIOS), limitador(5));

        servicio.buscar("calle larios");
        List<Lugar> segunda = servicio.buscar("calle larios");

        assertThat(segunda).containsExactly(LARIOS);
        assertThat(busquedas).hasSize(1);
    }

    @Test
    void lasMayusculasYLosEspaciosDeMasNoCuentanComoOtraBusqueda() {
        ServicioLugares servicio = new ServicioLugares(proveedorQueDevuelve(LARIOS), limitador(5));

        servicio.buscar("calle larios");
        servicio.buscar("  Calle   LARIOS ");

        assertThat(busquedas).containsExactly("calle larios");
    }

    @Test
    void siSeSuperaElLimiteDePeticionesNoSeLlamaAlProveedor() {
        ServicioLugares servicio = new ServicioLugares(proveedorQueDevuelve(LARIOS), limitador(1));
        servicio.buscar("calle larios");

        assertThatThrownBy(() -> servicio.buscar("alameda principal"))
                .isInstanceOf(BusquedaNoDisponibleException.class);
        assertThat(busquedas).containsExactly("calle larios");
    }

    @Test
    void unaBusquedaGuardadaSeSirveAunqueSeHayaAlcanzadoElLimite() {
        ServicioLugares servicio = new ServicioLugares(proveedorQueDevuelve(LARIOS), limitador(1));
        servicio.buscar("calle larios");

        assertThat(servicio.buscar("calle larios")).containsExactly(LARIOS);
    }

    @Test
    void siElProveedorFallaLaBusquedaNoEstaDisponible() {
        ProveedorLugares caido =
                texto -> {
                    throw new IOException("Nominatim no responde");
                };
        ServicioLugares servicio = new ServicioLugares(caido, limitador(1));

        assertThatThrownBy(() -> servicio.buscar("calle larios"))
                .isInstanceOf(BusquedaNoDisponibleException.class);
    }

    @Test
    void unFalloDelProveedorNoSeGuardaYSeReintentaEnLaSiguienteBusqueda() {
        AtomicInteger llamadas = new AtomicInteger();
        ProveedorLugares fallaLaPrimeraVez =
                texto -> {
                    if (llamadas.incrementAndGet() == 1) {
                        throw new IOException("Nominatim no responde");
                    }
                    return List.of(LARIOS);
                };
        ServicioLugares servicio = new ServicioLugares(fallaLaPrimeraVez, limitador(5));

        assertThatThrownBy(() -> servicio.buscar("calle larios"))
                .isInstanceOf(BusquedaNoDisponibleException.class);
        assertThat(servicio.buscar("calle larios")).containsExactly(LARIOS);
        assertThat(llamadas).hasValue(2);
    }
}
