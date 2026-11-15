package es.uma.weather.mapa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;

class TeselaTest {

    @Test
    void rechazaUnZoomFueraDeRango() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Tesela(-1, 0, 0));
        assertThatIllegalArgumentException().isThrownBy(() -> new Tesela(20, 0, 0));
    }

    @Test
    void rechazaCoordenadasQueNoExistenEnEseZoom() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Tesela(1, 2, 0));
        assertThatIllegalArgumentException().isThrownBy(() -> new Tesela(1, 0, -1));
    }

    @Test
    void laTeselaDelCentroDeMalagaCortaMalaga() {
        assertThat(new Tesela(13, 3995, 3196).cortaMalaga()).isTrue();
    }

    @Test
    void laTeselaDelMundoEnteroCortaMalaga() {
        assertThat(new Tesela(0, 0, 0).cortaMalaga()).isTrue();
    }

    @Test
    void unaTeselaDeMadridNoCortaMalaga() {
        assertThat(new Tesela(13, 4011, 3088).cortaMalaga()).isFalse();
    }
}
