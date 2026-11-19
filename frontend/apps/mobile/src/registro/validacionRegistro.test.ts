import { validarRegistro } from './validacionRegistro';

const DATOS_VALIDOS = {
  correo: 'persona@example.com',
  contrasena: '12345678',
  confirmaEdadYPolitica: true,
};

describe('validarRegistro', () => {
  it('no devuelve errores cuando los datos son válidos', () => {
    expect(validarRegistro(DATOS_VALIDOS)).toEqual({});
  });

  it('pide el correo cuando está vacío o solo tiene espacios', () => {
    expect(validarRegistro({ ...DATOS_VALIDOS, correo: '' }).correo).toBe(
      'Escribe tu correo electrónico',
    );
    expect(validarRegistro({ ...DATOS_VALIDOS, correo: '   ' }).correo).toBe(
      'Escribe tu correo electrónico',
    );
  });

  it.each(['persona', 'persona@', '@example.com', 'persona@example', 'per sona@example.com'])(
    'rechaza el correo con formato incorrecto «%s»',
    (correo) => {
      expect(validarRegistro({ ...DATOS_VALIDOS, correo }).correo).toBe(
        'Escribe un correo válido, como nombre@dominio.com',
      );
    },
  );

  it('acepta el correo con espacios al principio o al final', () => {
    expect(validarRegistro({ ...DATOS_VALIDOS, correo: '  persona@example.com ' })).toEqual({});
  });

  it('pide la contraseña cuando está vacía', () => {
    expect(validarRegistro({ ...DATOS_VALIDOS, contrasena: '' }).contrasena).toBe(
      'Escribe una contraseña',
    );
  });

  it('exige al menos 8 caracteres en la contraseña', () => {
    expect(validarRegistro({ ...DATOS_VALIDOS, contrasena: '1234567' }).contrasena).toBe(
      'La contraseña debe tener al menos 8 caracteres',
    );
    expect(
      validarRegistro({ ...DATOS_VALIDOS, contrasena: '12345678' }).contrasena,
    ).toBeUndefined();
  });

  it('cuenta como un carácter cada emoji de la contraseña', () => {
    expect(validarRegistro({ ...DATOS_VALIDOS, contrasena: '🌞🌞🌞🌞' }).contrasena).toBe(
      'La contraseña debe tener al menos 8 caracteres',
    );
  });

  it('rechaza una contraseña de más de 72 bytes, el máximo que admite el servidor', () => {
    // Con tildes, 37 caracteres ya son 74 bytes.
    expect(validarRegistro({ ...DATOS_VALIDOS, contrasena: 'á'.repeat(37) }).contrasena).toBe(
      'La contraseña es demasiado larga',
    );
    expect(
      validarRegistro({ ...DATOS_VALIDOS, contrasena: 'a'.repeat(72) }).contrasena,
    ).toBeUndefined();
  });

  it('exige confirmar la edad y la lectura de la política de privacidad', () => {
    expect(
      validarRegistro({ ...DATOS_VALIDOS, confirmaEdadYPolitica: false }).confirmaEdadYPolitica,
    ).toBe('Para crear la cuenta, confirma que tienes 14 años o más y que has leído la política');
  });

  it('devuelve todos los errores a la vez', () => {
    expect(
      Object.keys(validarRegistro({ correo: '', contrasena: '', confirmaEdadYPolitica: false })),
    ).toEqual(['correo', 'contrasena', 'confirmaEdadYPolitica']);
  });
});
