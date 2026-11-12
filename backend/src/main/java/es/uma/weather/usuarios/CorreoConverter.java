package es.uma.weather.usuarios;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Guarda un {@link Correo} como texto en la columna y lo vuelve a crear al leerlo. */
@Converter(autoApply = true)
class CorreoConverter implements AttributeConverter<Correo, String> {

    @Override
    public String convertToDatabaseColumn(Correo correo) {
        return correo == null ? null : correo.valor();
    }

    @Override
    public Correo convertToEntityAttribute(String valor) {
        return valor == null ? null : new Correo(valor);
    }
}
