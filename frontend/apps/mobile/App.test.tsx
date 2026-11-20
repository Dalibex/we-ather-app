import { act, fireEvent, render, screen } from '@testing-library/react-native';
import * as SecureStore from 'expo-secure-store';
import { BackHandler } from 'react-native';

jest.mock('expo-file-system/legacy', () => ({
  cacheDirectory: 'file:///cache/',
}));

import App from './App';
import { guardarToken, leerTokenGuardado } from './src/sesion/sesion';
import { tokenDePrueba } from './pruebas/tokenDePrueba';

// El sustituto de las pruebas (__mocks__/expo-secure-store.ts) guarda en memoria.
const { __vaciar } = SecureStore as unknown as { __vaciar: () => void };

/** Simula la respuesta del servidor a cualquier petición. */
function responderAlLogin(status: number, cuerpo: unknown) {
  globalThis.fetch = jest.fn(async () => ({
    ok: status >= 200 && status < 300,
    status,
    json: async () => cuerpo,
  })) as unknown as typeof fetch;
}

async function crearCuentaCon(correo: string, contrasena: string) {
  await fireEvent.changeText(screen.getByLabelText('Correo electrónico'), correo);
  await fireEvent.changeText(screen.getByLabelText('Contraseña'), contrasena);
  await fireEvent.press(screen.getByRole('checkbox'));
  await pulsar('Crear cuenta');
}

async function iniciarSesionCon(correo: string, contrasena: string) {
  await fireEvent.changeText(screen.getByLabelText('Correo electrónico'), correo);
  await fireEvent.changeText(screen.getByLabelText('Contraseña'), contrasena);
  await pulsar('Iniciar sesión');
}

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
  const fetchOriginal = globalThis.fetch;
  beforeEach(() => {
    __vaciar();
    responderAlLogin(401, { codigo: 'CREDENCIALES_INVALIDAS' });
  });
  afterEach(() => {
    jest.restoreAllMocks();
    globalThis.fetch = fetchOriginal;
  });

  it('al iniciar sesión guarda el token y lleva al mapa con «Cerrar sesión»', async () => {
    const token = tokenDePrueba(3600);
    responderAlLogin(200, { token, tipo: 'Bearer' });
    await render(<App />);
    await pulsar('Iniciar sesión');

    await iniciarSesionCon('persona@example.com', 'contraseña-de-prueba');

    expect(await screen.findByTestId('mapa-base')).toBeOnTheScreen();
    expect(screen.getByRole('button', { name: 'Cerrar sesión' })).toBeOnTheScreen();
    expect(await leerTokenGuardado()).toBe(token);
  });

  it('al crear la cuenta lo confirma y lleva al inicio de sesión con el correo escrito', async () => {
    responderAlLogin(201, { id: '00000000-0000-0000-0000-000000000000' });
    await render(<App />);
    await pulsar('Crear cuenta');

    await crearCuentaCon('persona@example.com', 'contraseña-de-prueba');

    expect(await screen.findByText('Cuenta creada. Ya puedes iniciar sesión.')).toBeOnTheScreen();
    expect(screen.getByRole('link', { name: 'Regístrate' })).toBeOnTheScreen();
    expect(screen.getByLabelText('Correo electrónico').props.value).toBe('persona@example.com');
    expect(globalThis.fetch).toHaveBeenCalledWith(
      'http://localhost:8080/api/v1/usuarios',
      expect.objectContaining({ method: 'POST' }),
    );
  });

  it('si el correo ya tiene cuenta lo dice y se queda en el registro', async () => {
    responderAlLogin(409, {
      codigo: 'CORREO_YA_REGISTRADO',
      mensaje: 'El correo ya está registrado',
    });
    await render(<App />);
    await pulsar('Crear cuenta');

    await crearCuentaCon('persona@example.com', 'contraseña-de-prueba');

    expect(await screen.findByRole('alert')).toHaveTextContent('El correo ya está registrado');
    expect(screen.getByRole('checkbox')).toBeOnTheScreen();
  });

  it('el aviso de cuenta creada no se queda si se vuelve más tarde al inicio de sesión', async () => {
    responderAlLogin(201, { id: '00000000-0000-0000-0000-000000000000' });
    await render(<App />);
    await pulsar('Crear cuenta');
    await crearCuentaCon('persona@example.com', 'contraseña-de-prueba');
    await screen.findByText('Cuenta creada. Ya puedes iniciar sesión.');

    await pulsar('Regístrate', 'link');
    await pulsar('Inicia sesión', 'link');

    expect(screen.queryByText('Cuenta creada. Ya puedes iniciar sesión.')).not.toBeOnTheScreen();
  });

  it('sin conexión con el servidor muestra un aviso y se queda en el inicio de sesión', async () => {
    globalThis.fetch = jest.fn(async () => {
      throw new TypeError('Network request failed');
    }) as unknown as typeof fetch;
    await render(<App />);
    await pulsar('Iniciar sesión');

    await iniciarSesionCon('persona@example.com', 'contraseña-de-prueba');

    expect(await screen.findByRole('alert')).toHaveTextContent(
      'No se ha podido conectar con el servidor. Comprueba tu conexión e inténtalo de nuevo.',
    );
  });

  it('si el móvil no puede guardar el token muestra un mensaje genérico en español', async () => {
    responderAlLogin(200, { token: tokenDePrueba(3600), tipo: 'Bearer' });
    (SecureStore.setItemAsync as jest.Mock).mockRejectedValueOnce(new Error('Keystore failure'));
    await render(<App />);
    await pulsar('Iniciar sesión');

    await iniciarSesionCon('persona@example.com', 'contraseña-de-prueba');

    expect(await screen.findByRole('alert')).toHaveTextContent(
      'No se ha podido iniciar sesión. Inténtalo de nuevo.',
    );
    expect(screen.queryByTestId('mapa-base')).not.toBeOnTheScreen();
  });

  it('mientras espera al servidor no envía dos veces', async () => {
    const pendiente = jest.fn(() => new Promise<Response>(() => {}));
    globalThis.fetch = pendiente as unknown as typeof fetch;
    await render(<App />);
    await pulsar('Iniciar sesión');

    await iniciarSesionCon('persona@example.com', 'contraseña-de-prueba');
    await pulsar('Iniciar sesión');

    expect(pendiente).toHaveBeenCalledTimes(1);
  });

  it('un error de un intento anterior no reaparece al volver al inicio de sesión', async () => {
    await render(<App />);
    await pulsar('Iniciar sesión');
    await iniciarSesionCon('persona@example.com', 'contraseña-incorrecta');
    await screen.findByRole('alert');

    await pulsar('Regístrate', 'link');
    await pulsar('Inicia sesión', 'link');

    expect(screen.queryByRole('alert')).not.toBeOnTheScreen();
  });

  it('una respuesta tardía no saca del registro a quien se fue a él mientras esperaba', async () => {
    let responder: (r: Response) => void = () => {};
    globalThis.fetch = jest.fn(
      () =>
        new Promise<Response>((resolver) => {
          responder = resolver;
        }),
    ) as unknown as typeof fetch;
    await render(<App />);
    await pulsar('Iniciar sesión');
    await iniciarSesionCon('persona@example.com', 'contraseña-de-prueba');

    await pulsar('Regístrate', 'link');
    await act(async () => {
      responder({
        ok: true,
        status: 200,
        json: async () => ({ token: tokenDePrueba(3600), tipo: 'Bearer' }),
      } as Response);
    });

    expect(screen.getByRole('checkbox')).toBeOnTheScreen();
  });

  it('si la sesión guardada se recupera después de pulsar «Continuar sin cuenta», el mapa la muestra iniciada', async () => {
    await guardarToken(tokenDePrueba(3600));
    const leer = SecureStore.getItemAsync as jest.Mock;
    const leerDeVerdad = leer.getMockImplementation() as (clave: string) => Promise<string | null>;
    let soltarLectura: () => void = () => {};
    leer.mockImplementationOnce(
      (clave: string) =>
        new Promise((resolver) => {
          soltarLectura = () => resolver(leerDeVerdad(clave));
        }),
    );
    await render(<App />);

    await pulsar('Continuar sin cuenta');
    await act(async () => soltarLectura());

    expect(await screen.findByRole('button', { name: 'Cerrar sesión' })).toBeOnTheScreen();
    expect(screen.getByTestId('mapa-base')).toBeOnTheScreen();
  });

  it('con credenciales incorrectas se queda en el inicio de sesión con un error genérico', async () => {
    await render(<App />);
    await pulsar('Iniciar sesión');

    await iniciarSesionCon('persona@example.com', 'contraseña-incorrecta');

    expect(await screen.findByRole('alert')).toHaveTextContent('Correo o contraseña incorrectos');
    expect(screen.queryByTestId('mapa-base')).not.toBeOnTheScreen();
  });

  it('con una sesión guardada abre directamente en el mapa con la sesión iniciada', async () => {
    await guardarToken(tokenDePrueba(3600));

    await render(<App />);

    expect(await screen.findByRole('button', { name: 'Cerrar sesión' })).toBeOnTheScreen();
    expect(screen.getByTestId('mapa-base')).toBeOnTheScreen();
  });

  it('«Cerrar sesión» borra el token y deja el mapa sin sesión', async () => {
    await guardarToken(tokenDePrueba(3600));
    await render(<App />);

    await fireEvent.press(await screen.findByRole('button', { name: 'Cerrar sesión' }));

    expect(await screen.findByRole('button', { name: 'Iniciar sesión' })).toBeOnTheScreen();
    expect(await leerTokenGuardado()).toBeNull();
  });

  it('si el móvil no puede borrar el token, la sesión se cierra igualmente', async () => {
    await guardarToken(tokenDePrueba(3600));
    (SecureStore.deleteItemAsync as jest.Mock).mockRejectedValueOnce(new Error('Keystore failure'));
    await render(<App />);

    await fireEvent.press(await screen.findByRole('button', { name: 'Cerrar sesión' }));

    expect(await screen.findByRole('button', { name: 'Iniciar sesión' })).toBeOnTheScreen();
  });

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

  it('con la sesión iniciada, el botón «atrás» en el mapa cierra la app y no vuelve a la bienvenida', async () => {
    await guardarToken(tokenDePrueba(3600));
    const atras = espiarAtras();
    await render(<App />);
    await screen.findByRole('button', { name: 'Cerrar sesión' });

    expect(await atras()).toBe(false);
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
