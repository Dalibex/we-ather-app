import { fireEvent, render, screen } from '@testing-library/react-native';

import { PantallaMapa } from './PantallaMapa';

describe('PantallaMapa', () => {
  it('muestra el mapa base con un botón para iniciar sesión', async () => {
    await render(<PantallaMapa onIniciarSesion={jest.fn()} />);

    expect(screen.getByTestId('mapa-base')).toBeOnTheScreen();
    expect(screen.getByRole('button', { name: 'Iniciar sesión' })).toBeOnTheScreen();
  });

  it('llama a onIniciarSesion al pulsar el botón', async () => {
    const onIniciarSesion = jest.fn();
    await render(<PantallaMapa onIniciarSesion={onIniciarSesion} />);

    await fireEvent.press(screen.getByRole('button', { name: 'Iniciar sesión' }));

    expect(onIniciarSesion).toHaveBeenCalledTimes(1);
  });
});
