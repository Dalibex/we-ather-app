import { fireEvent, render, screen, within } from '@testing-library/react-native';
import { StyleSheet, Text } from 'react-native';

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

  it('no muestra el botón de volver si no recibe onVolver', async () => {
    await render(
      <MarcoAutenticacion titulo="Título" subtitulo="Subtítulo">
        <Text>Contenido</Text>
      </MarcoAutenticacion>,
    );

    expect(screen.queryByRole('button', { name: 'Volver' })).not.toBeOnTheScreen();
  });

  async function renderMarco() {
    await render(
      <MarcoAutenticacion titulo="Título" subtitulo="Subtítulo" onVolver={jest.fn()}>
        <Text>Contenido</Text>
      </MarcoAutenticacion>,
    );
  }

  it('coloca «Volver» por debajo de la barra de estado', async () => {
    await renderMarco();

    // El mock de react-native-safe-area-context simula 24 px de barra de estado arriba.
    const estilo = StyleSheet.flatten(screen.getByRole('button', { name: 'Volver' }).props.style);
    expect(estilo.top).toBe(24 + 8);
  });

  it('deja desplazar el formulario si no cabe y pulsar sus botones con el teclado abierto', async () => {
    await renderMarco();

    const desplazable = screen.getByTestId('marco-desplazable');
    expect(within(desplazable).getByText('Contenido')).toBeOnTheScreen();
    expect(desplazable.props.keyboardShouldPersistTaps).toBe('handled');
  });

  it('deja «Volver» fuera de la parte que se desplaza, para que el teclado no lo esconda', async () => {
    await renderMarco();

    const desplazable = screen.getByTestId('marco-desplazable');
    expect(within(desplazable).queryByRole('button', { name: 'Volver' })).not.toBeOnTheScreen();
  });
});
