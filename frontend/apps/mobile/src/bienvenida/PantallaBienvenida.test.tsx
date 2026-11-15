import { fireEvent, render, screen } from '@testing-library/react-native';

import { PantallaBienvenida } from './PantallaBienvenida';

function renderPantalla(props: Partial<Parameters<typeof PantallaBienvenida>[0]> = {}) {
  return render(
    <PantallaBienvenida
      onIniciarSesion={jest.fn()}
      onCrearCuenta={jest.fn()}
      onContinuarSinCuenta={jest.fn()}
      {...props}
    />,
  );
}

describe('PantallaBienvenida', () => {
  it('ofrece iniciar sesión, crear una cuenta o continuar sin cuenta', async () => {
    await renderPantalla();

    expect(screen.getByText('WEather')).toBeOnTheScreen();
    expect(screen.getByRole('button', { name: 'Iniciar sesión' })).toBeOnTheScreen();
    expect(screen.getByRole('button', { name: 'Crear cuenta' })).toBeOnTheScreen();
    expect(screen.getByRole('button', { name: 'Continuar sin cuenta' })).toBeOnTheScreen();
  });

  it('no tiene botón de volver, porque es la primera pantalla', async () => {
    await renderPantalla();

    expect(screen.queryByRole('button', { name: 'Volver' })).not.toBeOnTheScreen();
  });

  it.each([
    ['Iniciar sesión', 'onIniciarSesion'],
    ['Crear cuenta', 'onCrearCuenta'],
    ['Continuar sin cuenta', 'onContinuarSinCuenta'],
  ] as const)('al pulsar «%s» llama a %s', async (boton, prop) => {
    const accion = jest.fn();
    await renderPantalla({ [prop]: accion });

    await fireEvent.press(screen.getByRole('button', { name: boton }));

    expect(accion).toHaveBeenCalledTimes(1);
  });
});
