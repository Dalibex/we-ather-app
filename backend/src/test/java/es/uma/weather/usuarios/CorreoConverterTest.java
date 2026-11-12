package es.uma.weather.usuarios;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CorreoConverterTest {

    private final CorreoConverter converter = new CorreoConverter();

    @Test
    void guardaElCorreoNormalizado() {
        assertThat(converter.convertToDatabaseColumn(new Correo("Usuario@Example.com")))
                .isEqualTo("usuario@example.com");
    }

    @Test
    void leeElCorreoDeLaColumna() {
        assertThat(converter.convertToEntityAttribute("usuario@example.com"))
                .isEqualTo(new Correo("usuario@example.com"));
    }

    @Test
    void conservaLosValoresNulos() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }
}
