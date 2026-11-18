import type { DatosRegistro } from './PantallaRegistro';

/** Mensaje de error de cada campo del registro; un campo sin error no aparece. */
export type ErroresRegistro = Partial<Record<keyof DatosRegistro, string>>;

export const LONGITUD_MINIMA_CONTRASENA = 8;

// Algo antes y después de una sola «@», sin espacios, y un dominio con punto. Si el correo existe
// de verdad, o si ya está registrado, lo comprueba el servidor (T-01.2).
const FORMATO_CORREO = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

/** Comprueba en la app el formulario de registro antes de enviarlo al servidor (HU-01). */
export function validarRegistro({
  correo,
  contrasena,
  confirmaEdadYPolitica,
}: DatosRegistro): ErroresRegistro {
  const errores: ErroresRegistro = {};
  const correoLimpio = correo.trim();

  if (correoLimpio === '') {
    errores.correo = 'Escribe tu correo electrónico';
  } else if (!FORMATO_CORREO.test(correoLimpio)) {
    errores.correo = 'Escribe un correo válido, como nombre@dominio.com';
  }

  // Solo longitud mínima, sin exigir tipos de carácter (decisión del equipo, como NIST SP 800-63B).
  // Se cuentan caracteres, no unidades UTF-16, para que un emoji valga uno.
  if (contrasena === '') {
    errores.contrasena = 'Escribe una contraseña';
  } else if ([...contrasena].length < LONGITUD_MINIMA_CONTRASENA) {
    errores.contrasena = `La contraseña debe tener al menos ${LONGITUD_MINIMA_CONTRASENA} caracteres`;
  }

  if (!confirmaEdadYPolitica) {
    errores.confirmaEdadYPolitica =
      'Para crear la cuenta, confirma que tienes 14 años o más y que has leído la política';
  }

  return errores;
}
