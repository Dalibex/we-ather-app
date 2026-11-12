import { render, screen } from '@testing-library/react-native';
import { StyleSheet } from 'react-native';

import { MapaBase } from './MapaBase';

describe('MapaBase', () => {
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

  it('muestra la atribución de OpenStreetMap', async () => {
    await render(<MapaBase />);

    expect(screen.getByText('© OpenStreetMap contributors')).toBeOnTheScreen();
  });

  // El mock de react-native-safe-area-context simula 48 px de botones de navegación abajo.
  it('coloca la atribución por encima de los botones de navegación del sistema', async () => {
    await render(<MapaBase />);

    const estilo = StyleSheet.flatten(screen.getByText('© OpenStreetMap contributors').props.style);
    expect(estilo.bottom).toBe(48);
  });

  it('deja el logo del mapa del sistema por encima de los botones de navegación', async () => {
    await render(<MapaBase />);

    expect(screen.getByTestId('mapa-base').props.mapPadding.bottom).toBe(48);
  });
});
