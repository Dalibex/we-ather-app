import { act, render, screen } from '@testing-library/react-native';
import { StyleSheet } from 'react-native';
import { animateToRegion } from '../../__mocks__/react-native-maps';

jest.mock('expo-file-system/legacy', () => ({
  get cacheDirectory() {
    return process.env.EXPO_TEST_CACHE_DIRECTORY === 'none' ? null : 'file:///cache/';
  },
}));

import { MapaBase } from './MapaBase';

describe('MapaBase', () => {
  afterEach(() => animateToRegion.mockClear());

  it('abre el mapa centrado en Málaga', async () => {
    await render(<MapaBase />);

    const { latitude, longitude } = screen.getByTestId('mapa-base').props.initialRegion;
    expect(latitude).toBeCloseTo(36.72, 1);
    expect(longitude).toBeCloseTo(-4.42, 1);
  });

  it('pide al servidor de WEather las teselas de OpenStreetMap, en lugar del mapa del sistema', async () => {
    await render(<MapaBase />);

    expect(screen.getByTestId('mapa-base').props.mapType).toBe('none');
    const teselas = screen.getByTestId('teselas-osm').props;
    expect(teselas.urlTemplate).toBe('http://localhost:8080/api/v1/teselas/{z}/{x}/{y}.png');
    expect(teselas.maximumZ).toBe(19);
    expect(teselas.shouldReplaceMapContent).toBe(true);
  });

  it('guarda las teselas localmente durante siete días', async () => {
    await render(<MapaBase />);

    const teselas = screen.getByTestId('teselas-osm').props;
    expect(teselas.tileCachePath).toBe('file:///cache/teselas-mapa-v2/');
    expect(teselas.tileCacheMaxAge).toBe(7 * 24 * 60 * 60);
    expect(teselas.offlineMode).toBe(false);
  });

  it('sigue mostrando el mapa si el sistema no proporciona un directorio de caché', async () => {
    const directorioAnterior = process.env.EXPO_TEST_CACHE_DIRECTORY;
    process.env.EXPO_TEST_CACHE_DIRECTORY = 'none';

    try {
      await render(<MapaBase />);

      expect(screen.getByTestId('mapa-base')).toBeOnTheScreen();
      expect(screen.getByTestId('teselas-osm').props.tileCachePath).toBeUndefined();
    } finally {
      if (directorioAnterior === undefined) {
        delete process.env.EXPO_TEST_CACHE_DIRECTORY;
      } else {
        process.env.EXPO_TEST_CACHE_DIRECTORY = directorioAnterior;
      }
    }
  });

  it('muestra la atribución de los proveedores de datos', async () => {
    await render(<MapaBase />);

    expect(screen.getByTestId('atribucion')).toBeOnTheScreen();
    expect(screen.getByText('© OpenStreetMap contributors')).toBeOnTheScreen();
  });

  // El mock de react-native-safe-area-context simula 48 px de botones de navegación abajo.
  it('coloca la atribución por encima de los botones de navegación del sistema', async () => {
    await render(<MapaBase />);

    const estilo = StyleSheet.flatten(screen.getByTestId('atribucion').props.style);
    expect(estilo.bottom).toBe(48);
  });

  it('deja el logo del mapa del sistema por encima de los botones de navegación', async () => {
    await render(<MapaBase />);

    expect(screen.getByTestId('mapa-base').props.mapPadding.bottom).toBe(48);
  });

  it('no deja alejar el mapa más allá de lo que ocupa Málaga', async () => {
    await render(<MapaBase />);

    expect(screen.getByTestId('mapa-base').props.minZoomLevel).toBe(11);
  });

  it('devuelve el mapa a Málaga si se arrastra fuera', async () => {
    await render(<MapaBase />);

    const madrid = {
      latitude: 40.4168,
      longitude: -3.7038,
      latitudeDelta: 0.05,
      longitudeDelta: 0.05,
    };
    await act(() => screen.getByTestId('mapa-base').props.onRegionChangeComplete(madrid, {}));

    expect(animateToRegion).toHaveBeenCalledTimes(1);
    expect(animateToRegion).toHaveBeenCalledWith({
      latitude: 36.85,
      longitude: -4.25,
      latitudeDelta: 0.05,
      longitudeDelta: 0.05,
    });
  });

  it('no mueve el mapa mientras se navega dentro de Málaga', async () => {
    await render(<MapaBase />);

    const centro = {
      latitude: 36.7213,
      longitude: -4.4214,
      latitudeDelta: 0.05,
      longitudeDelta: 0.05,
    };
    await act(() => screen.getByTestId('mapa-base').props.onRegionChangeComplete(centro, {}));

    expect(animateToRegion).not.toHaveBeenCalled();
  });
});
