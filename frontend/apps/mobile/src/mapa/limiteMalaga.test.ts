import type { Region } from 'react-native-maps';

import { LIMITE_MALAGA, ajustarAMalaga } from './limiteMalaga';

const region = (latitude: number, longitude: number): Region => ({
  latitude,
  longitude,
  latitudeDelta: 0.05,
  longitudeDelta: 0.05,
});

describe('ajustarAMalaga', () => {
  it('deja igual una región centrada dentro de Málaga', () => {
    const centro = region(36.7213, -4.4214);

    expect(ajustarAMalaga(centro)).toBe(centro);
  });

  it('lleva al borde de Málaga una región centrada fuera', () => {
    expect(ajustarAMalaga(region(40.4168, -3.7038))).toMatchObject({
      latitude: LIMITE_MALAGA.norte,
      longitude: LIMITE_MALAGA.este,
    });
    expect(ajustarAMalaga(region(36.5, -4.9))).toMatchObject({
      latitude: LIMITE_MALAGA.sur,
      longitude: LIMITE_MALAGA.oeste,
    });
  });

  it('solo corrige la coordenada que se sale', () => {
    expect(ajustarAMalaga(region(36.7, -5.0))).toMatchObject({
      latitude: 36.7,
      longitude: LIMITE_MALAGA.oeste,
    });
  });

  it('mantiene el zoom de la región', () => {
    const ajustada = ajustarAMalaga({ ...region(37.5, -4.4), latitudeDelta: 0.2 });

    expect(ajustada.latitudeDelta).toBe(0.2);
    expect(ajustada.longitudeDelta).toBe(0.05);
  });

  // Mismo rectángulo con el que el servidor decide qué teselas sirve (Tesela.java).
  it('usa el rectángulo del término municipal que usa el servidor para las teselas', () => {
    expect(LIMITE_MALAGA).toEqual({ oeste: -4.65, este: -4.25, sur: 36.6, norte: 36.85 });
  });
});
