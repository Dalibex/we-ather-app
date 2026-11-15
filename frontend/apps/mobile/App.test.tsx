import { fireEvent, render, screen } from '@testing-library/react-native';

import App from './App';

async function pulsar(nombre: string, rol: 'button' | 'link' = 'button') {
  await fireEvent.press(screen.getByRole(rol, { name: nombre }));
}

describe('App', () => {
  afterEach(() => jest.restoreAllMocks());

  it('abre en la pantalla de bienvenida, no en el mapa', async () => {
    await render(<App />);

    expect(screen.getByRole('button', { name: 'Continuar sin cuenta' })).toBeOnTheScreen();
    expect(screen.queryByTestId('mapa-base')).not.toBeOnTheScreen();
  });

  it('lleva al mapa sin cuenta al pulsar «Continuar sin cuenta»', async () => {
    await render(<App />);

    await pulsar('Continuar sin cuenta');

    expect(screen.getByTestId('mapa-base')).toBeOnTheScreen();
  });

  it('abre el registro desde la bienvenida y vuelve a ella', async () => {
    await render(<App />);

    await pulsar('Crear cuenta');
    expect(screen.getByRole('checkbox')).toBeOnTheScreen();

    await pulsar('Volver');
    expect(screen.getByRole('button', { name: 'Continuar sin cuenta' })).toBeOnTheScreen();
  });

  it('abre el inicio de sesión desde la bienvenida y vuelve a ella', async () => {
    await render(<App />);

    await pulsar('Iniciar sesión');
    expect(screen.getByRole('link', { name: 'Regístrate' })).toBeOnTheScreen();

    await pulsar('Volver');
    expect(screen.getByRole('button', { name: 'Continuar sin cuenta' })).toBeOnTheScreen();
  });

  it('pasa del inicio de sesión al registro y del registro al inicio de sesión', async () => {
    await render(<App />);
    await pulsar('Iniciar sesión');

    await pulsar('Regístrate', 'link');
    expect(screen.getByRole('checkbox')).toBeOnTheScreen();

    await pulsar('Inicia sesión', 'link');
    expect(screen.getByRole('link', { name: 'Regístrate' })).toBeOnTheScreen();
  });

  it('desde el mapa abre el inicio de sesión y «Volver» regresa al mapa', async () => {
    await render(<App />);
    await pulsar('Continuar sin cuenta');

    await pulsar('Iniciar sesión');
    expect(screen.getByRole('link', { name: 'Regístrate' })).toBeOnTheScreen();
    expect(screen.queryByTestId('mapa-base')).not.toBeOnTheScreen();

    await pulsar('Volver');
    expect(screen.getByTestId('mapa-base')).toBeOnTheScreen();
  });

  it.each([
    ['Crear cuenta', 'Crear cuenta'],
    ['Iniciar sesión', 'Iniciar sesión'],
  ])(
    'no escribe la contraseña en la consola al enviar el formulario de «%s»',
    async (botonBienvenida, botonEnviar) => {
      const consola = ['log', 'info', 'warn', 'error', 'debug'].map((metodo) =>
        jest.spyOn(console, metodo as 'log').mockImplementation(() => {}),
      );
      await render(<App />);
      await pulsar(botonBienvenida);

      await fireEvent.changeText(screen.getByLabelText('Contraseña'), 'contraseña-de-prueba');
      await pulsar(botonEnviar);

      const escrito = consola
        .flatMap((espia) => espia.mock.calls.flat())
        .map((valor) => JSON.stringify(valor));
      expect(escrito.join(' ')).not.toContain('contraseña-de-prueba');
    },
  );
});
