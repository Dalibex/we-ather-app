import { render, screen } from '@testing-library/react-native';

import { MapaBase } from './MapaBase';

describe('MapaBase', () => {
  it('abre el mapa centrado en Málaga', async () => {
    await render(<MapaBase />);

    const { latitude, longitude } = screen.getByTestId('mapa-base').props.initialRegion;
    expect(latitude).toBeCloseTo(36.72, 1);
    expect(longitude).toBeCloseTo(-4.42, 1);
  });

  it('pinta las teselas de OpenStreetMap en lugar del mapa del sistema', async () => {
    await render(<MapaBase />);

    expect(screen.getByTestId('mapa-base').props.mapType).toBe('none');
    const teselas = screen.getByTestId('teselas-osm').props;
    expect(teselas.urlTemplate).toBe('https://tile.openstreetmap.org/{z}/{x}/{y}.png');
    expect(teselas.maximumZ).toBe(19);
    expect(teselas.shouldReplaceMapContent).toBe(true);
  });

  it('muestra la atribución de OpenStreetMap', async () => {
    await render(<MapaBase />);

    expect(screen.getByText('© OpenStreetMap contributors')).toBeOnTheScreen();
  });
});
