import { fireEvent, render, screen } from '@testing-library/react-native';

import App from './App';

describe('App', () => {
  afterEach(() => jest.restoreAllMocks());

  it('abre la pantalla de registro desde la pantalla inicial', async () => {
    await render(<App />);

    await fireEvent.press(screen.getByRole('button', { name: 'Crear cuenta' }));

    expect(screen.getByLabelText('Correo electrónico')).toBeOnTheScreen();
  });

  it('vuelve a la pantalla inicial desde la pantalla de registro', async () => {
    await render(<App />);
    await fireEvent.press(screen.getByRole('button', { name: 'Crear cuenta' }));

    await fireEvent.press(screen.getByRole('button', { name: 'Volver' }));

    expect(screen.queryByLabelText('Correo electrónico')).not.toBeOnTheScreen();
    expect(screen.getByRole('button', { name: 'Crear cuenta' })).toBeOnTheScreen();
  });

  it('abre la pantalla de inicio de sesión desde la pantalla inicial y vuelve', async () => {
    await render(<App />);

    await fireEvent.press(screen.getByRole('button', { name: 'Iniciar sesión' }));
    expect(screen.getByRole('link', { name: 'Regístrate' })).toBeOnTheScreen();

    await fireEvent.press(screen.getByRole('button', { name: 'Volver' }));
    expect(screen.queryByRole('link', { name: 'Regístrate' })).not.toBeOnTheScreen();
    expect(screen.getByRole('button', { name: 'Iniciar sesión' })).toBeOnTheScreen();
  });

  it('pasa del inicio de sesión al registro y del registro al inicio de sesión', async () => {
    await render(<App />);
    await fireEvent.press(screen.getByRole('button', { name: 'Iniciar sesión' }));

    await fireEvent.press(screen.getByRole('link', { name: 'Regístrate' }));
    expect(screen.getByRole('checkbox')).toBeOnTheScreen();

    await fireEvent.press(screen.getByRole('link', { name: 'Inicia sesión' }));
    expect(screen.getByRole('link', { name: 'Regístrate' })).toBeOnTheScreen();
  });

  it.each([
    ['Crear cuenta', 'Crear cuenta'],
    ['Iniciar sesión', 'Iniciar sesión'],
  ])(
    'no escribe la contraseña en la consola al enviar el formulario de «%s»',
    async (botonInicio, botonEnviar) => {
      const consola = ['log', 'info', 'warn', 'error', 'debug'].map((metodo) =>
        jest.spyOn(console, metodo as 'log').mockImplementation(() => {}),
      );
      await render(<App />);
      await fireEvent.press(screen.getByRole('button', { name: botonInicio }));

      await fireEvent.changeText(screen.getByLabelText('Contraseña'), 'contraseña-de-prueba');
      await fireEvent.press(screen.getByRole('button', { name: botonEnviar }));

      const escrito = consola
        .flatMap((espia) => espia.mock.calls.flat())
        .map((v) => JSON.stringify(v));
      expect(escrito.join(' ')).not.toContain('contraseña-de-prueba');
    },
  );
});
