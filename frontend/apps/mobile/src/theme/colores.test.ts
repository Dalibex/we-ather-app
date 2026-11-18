import { colores } from './colores';

type Rgb = [number, number, number];

function hexARgb(hex: string): Rgb {
  const valor = parseInt(hex.slice(1), 16);
  return [(valor >> 16) & 255, (valor >> 8) & 255, valor & 255];
}

/** La tarjeta es blanca semitransparente: su color real es la mezcla con el fondo de la pantalla. */
function colorTarjeta(): Rgb {
  const [, r, g, b, a] = /rgba\((\d+), (\d+), (\d+), ([\d.]+)\)/.exec(colores.tarjeta) ?? [];
  const alfa = Number(a);
  const fondo = hexARgb(colores.fondo);
  return [Number(r), Number(g), Number(b)].map(
    (canal, i) => canal * alfa + fondo[i]! * (1 - alfa),
  ) as Rgb;
}

/** Luminancia relativa y relación de contraste según WCAG 2.1. */
function luminancia(rgb: Rgb): number {
  const [r, g, b] = rgb.map((canal) => {
    const c = canal / 255;
    return c <= 0.03928 ? c / 12.92 : ((c + 0.055) / 1.055) ** 2.4;
  }) as Rgb;
  return 0.2126 * r + 0.7152 * g + 0.0722 * b;
}

function contraste(texto: Rgb, fondo: Rgb): number {
  const [clara, oscura] = [luminancia(texto), luminancia(fondo)].sort((x, y) => y - x);
  return (clara! + 0.05) / (oscura! + 0.05);
}

// WCAG AA pide 4,5:1 para texto normal.
const MINIMO_AA = 4.5;

describe('colores', () => {
  it.each(['textoPrincipal', 'textoSecundario', 'textoSuave', 'verde', 'error'] as const)(
    'el texto %s se lee sobre la tarjeta con contraste AA',
    (nombre) => {
      expect(contraste(hexARgb(colores[nombre]), colorTarjeta())).toBeGreaterThanOrEqual(MINIMO_AA);
    },
  );

  it.each(['textoPrincipal', 'textoSecundario', 'textoSuave', 'verde', 'error'] as const)(
    'el texto %s se lee sobre el fondo con contraste AA',
    (nombre) => {
      expect(contraste(hexARgb(colores[nombre]), hexARgb(colores.fondo))).toBeGreaterThanOrEqual(
        MINIMO_AA,
      );
    },
  );
});
