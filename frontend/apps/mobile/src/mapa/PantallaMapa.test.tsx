import { fireEvent, render, screen } from '@testing-library/react-native';

jest.mock('expo-file-system/legacy', () => ({
  cacheDirectory: 'file:///cache/',
}));

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

  it('con la sesión iniciada muestra «Cerrar sesión» en lugar de «Iniciar sesión»', async () => {
    const onCerrarSesion = jest.fn();
    await render(<PantallaMapa onIniciarSesion={jest.fn()} onCerrarSesion={onCerrarSesion} />);

    expect(screen.queryByRole('button', { name: 'Iniciar sesión' })).not.toBeOnTheScreen();
    await fireEvent.press(screen.getByRole('button', { name: 'Cerrar sesión' }));

    expect(onCerrarSesion).toHaveBeenCalledTimes(1);
  });
});
