import type { Region } from 'react-native-maps';

/**
 * Rectángulo que contiene el término municipal de Málaga, con margen. Es el mismo con el que el
 * servidor decide qué teselas sirve (`Tesela.java`): fuera de él, el mapa sale vacío.
 */
export const LIMITE_MALAGA = { oeste: -4.65, este: -4.25, sur: 36.6, norte: 36.85 };

/** Último nivel de zoom al que se puede alejar el mapa: en él cabe Málaga entera. */
export const ZOOM_MINIMO_MALAGA = 11;

const limitar = (valor: number, minimo: number, maximo: number) =>
  Math.min(Math.max(valor, minimo), maximo);

/**
 * Devuelve la región con el centro llevado al borde de Málaga si se ha salido, sin cambiar el
 * zoom. Si el centro ya está dentro, devuelve la misma región.
 */
export function ajustarAMalaga(region: Region): Region {
  const latitude = limitar(region.latitude, LIMITE_MALAGA.sur, LIMITE_MALAGA.norte);
  const longitude = limitar(region.longitude, LIMITE_MALAGA.oeste, LIMITE_MALAGA.este);
  if (latitude === region.latitude && longitude === region.longitude) {
    return region;
  }
  return { ...region, latitude, longitude };
}
