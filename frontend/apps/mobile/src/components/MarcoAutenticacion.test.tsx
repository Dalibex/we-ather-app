import { fireEvent, render, screen } from '@testing-library/react-native';
import { Text } from 'react-native';

import { MarcoAutenticacion } from './MarcoAutenticacion';

describe('MarcoAutenticacion', () => {
  it('muestra el título, el subtítulo y el contenido del formulario', async () => {
    await render(
      <MarcoAutenticacion
        titulo="Título de prueba"
        subtitulo="Subtítulo de prueba"
        onVolver={jest.fn()}
      >
        <Text>Contenido del formulario</Text>
      </MarcoAutenticacion>,
    );

    expect(screen.getByText('Título de prueba')).toBeOnTheScreen();
    expect(screen.getByText('Subtítulo de prueba')).toBeOnTheScreen();
    expect(screen.getByText('Contenido del formulario')).toBeOnTheScreen();
  });

  it('llama a onVolver al pulsar volver', async () => {
    const onVolver = jest.fn();
    await render(
      <MarcoAutenticacion titulo="Título" subtitulo="Subtítulo" onVolver={onVolver}>
        <Text>Contenido</Text>
      </MarcoAutenticacion>,
    );

    await fireEvent.press(screen.getByRole('button', { name: 'Volver' }));

    expect(onVolver).toHaveBeenCalledTimes(1);
  });
});
