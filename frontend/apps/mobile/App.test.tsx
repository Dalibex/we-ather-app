import { act, fireEvent, render, screen } from '@testing-library/react-native';
import { BackHandler } from 'react-native';

import App from './App';

/** Simula el botón o gesto «atrás» de Android: llama al último manejador que registró la app. */
function espiarAtras() {
  type Manejador = Parameters<typeof BackHandler.addEventListener>[1];
  const manejadores: Manejador[] = [];
  jest.spyOn(BackHandler, 'addEventListener').mockImplementation((_evento, manejador) => {
    manejadores.push(manejador);
    return {
      remove: () => {
        manejadores.splice(manejadores.indexOf(manejador), 1);
      },
    };
  });
  return async () => {
    let atendido: boolean | null | undefined = false;
    await act(async () => {
      const manejador = manejadores.at(-1);
      atendido = manejador ? manejador({} as Parameters<Manejador>[0]) : false;
    });
    return atendido;
  };
}

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

  it('el botón «atrás» de Android vuelve del registro a la bienvenida', async () => {
    const atras = espiarAtras();
    await render(<App />);
    await pulsar('Crear cuenta');

    expect(await atras()).toBe(true);

    expect(screen.getByRole('button', { name: 'Continuar sin cuenta' })).toBeOnTheScreen();
  });

  it('el botón «atrás» de Android vuelve del inicio de sesión al mapa si se entró desde él', async () => {
    const atras = espiarAtras();
    await render(<App />);
    await pulsar('Continuar sin cuenta');
    await pulsar('Iniciar sesión');

    expect(await atras()).toBe(true);

    expect(screen.getByTestId('mapa-base')).toBeOnTheScreen();
  });

  it('el botón «atrás» de Android vuelve del mapa a la bienvenida', async () => {
    const atras = espiarAtras();
    await render(<App />);
    await pulsar('Continuar sin cuenta');

    expect(await atras()).toBe(true);

    expect(screen.getByRole('button', { name: 'Continuar sin cuenta' })).toBeOnTheScreen();
  });

  it('en la bienvenida deja que el botón «atrás» cierre la app, como siempre', async () => {
    const atras = espiarAtras();
    await render(<App />);

    expect(await atras()).toBe(false);
  });

  it('conserva el correo, pero no la contraseña, al pasar del inicio de sesión al registro y volver', async () => {
    await render(<App />);
    await pulsar('Iniciar sesión');
    await fireEvent.changeText(screen.getByLabelText('Correo electrónico'), 'persona@example.com');
    await fireEvent.changeText(screen.getByLabelText('Contraseña'), 'contraseña-de-prueba');

    await pulsar('Regístrate', 'link');
    expect(screen.getByLabelText('Correo electrónico').props.value).toBe('persona@example.com');
    expect(screen.getByLabelText('Contraseña').props.value).toBe('');

    await fireEvent.changeText(screen.getByLabelText('Correo electrónico'), 'otra@example.com');
    await pulsar('Inicia sesión', 'link');
    expect(screen.getByLabelText('Correo electrónico').props.value).toBe('otra@example.com');
  });

  it('«Volver» tras pasar del inicio de sesión al registro regresa a la pantalla de origen', async () => {
    await render(<App />);
    await pulsar('Continuar sin cuenta');
    await pulsar('Iniciar sesión');
    await pulsar('Regístrate', 'link');

    await pulsar('Volver');

    expect(screen.getByTestId('mapa-base')).toBeOnTheScreen();
  });
});
