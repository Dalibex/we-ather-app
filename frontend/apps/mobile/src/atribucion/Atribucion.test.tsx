import { fireEvent, render, screen } from '@testing-library/react-native';
import { Linking } from 'react-native';

import { Atribucion } from './Atribucion';

describe('Atribucion', () => {
  const proveedores = [
    ['© OpenStreetMap contributors', 'https://www.openstreetmap.org/copyright'],
    ['Open-Meteo.com', 'https://open-meteo.com/'],
    ['© AEMET', 'https://www.aemet.es/es/nota_legal'],
    ['Ayuntamiento de Málaga', 'https://datosabiertos.malaga.eu/'],
  ];

  it.each(proveedores)('muestra la atribución de %s', async (texto) => {
    await render(<Atribucion />);

    expect(screen.getByText(texto)).toBeOnTheScreen();
  });

  it.each(proveedores)('%s enlaza a su licencia', async (texto, licencia) => {
    const abrirEnlace = jest.spyOn(Linking, 'openURL').mockResolvedValue(undefined);
    await render(<Atribucion />);

    fireEvent.press(screen.getByText(texto));

    expect(abrirEnlace).toHaveBeenCalledWith(licencia);
  });
});
