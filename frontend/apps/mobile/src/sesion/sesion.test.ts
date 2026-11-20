import * as SecureStore from 'expo-secure-store';

import {
  borrarToken,
  guardarToken,
  iniciarSesion,
  leerTokenGuardado,
  mensajeParaElUsuario,
  registrar,
} from './sesion';
import { tokenDePrueba } from './tokenDePrueba';

// El sustituto de las pruebas (__mocks__/expo-secure-store.ts) guarda en memoria.
const { __vaciar } = SecureStore as unknown as { __vaciar: () => void };

function respuesta(status: number, cuerpo: unknown) {
  return { ok: status >= 200 && status < 300, status, json: async () => cuerpo } as Response;
}

describe('iniciarSesion', () => {
  const fetchOriginal = globalThis.fetch;
  afterEach(() => {
    globalThis.fetch = fetchOriginal;
  });

  it('envía el correo y la contraseña al servidor y devuelve el token', async () => {
    const fetch = jest.fn(async () => respuesta(200, { token: 'el-token', tipo: 'Bearer' }));
    globalThis.fetch = fetch as unknown as typeof fetch;

    const token = await iniciarSesion('persona@example.com', 'contraseña-de-prueba');

    expect(token).toBe('el-token');
    expect(fetch).toHaveBeenCalledWith('http://localhost:8080/api/v1/sesiones', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ correo: 'persona@example.com', contrasena: 'contraseña-de-prueba' }),
    });
  });

  it('con credenciales incorrectas da un mensaje genérico', async () => {
    globalThis.fetch = jest.fn(async () =>
      respuesta(401, {
        codigo: 'CREDENCIALES_INVALIDAS',
        mensaje: 'Correo o contraseña incorrectos',
      }),
    ) as unknown as typeof fetch;

    await expect(iniciarSesion('persona@example.com', 'mal')).rejects.toThrow(
      'Correo o contraseña incorrectos',
    );
  });

  it('si faltan datos muestra el mensaje del servidor', async () => {
    globalThis.fetch = jest.fn(async () =>
      respuesta(400, { codigo: 'DATOS_INVALIDOS', mensaje: 'El correo es obligatorio' }),
    ) as unknown as typeof fetch;

    await expect(iniciarSesion('', 'contraseña-de-prueba')).rejects.toThrow(
      'El correo es obligatorio',
    );
  });

  it('si el servidor falla pide volver a intentarlo', async () => {
    globalThis.fetch = jest.fn(async () => respuesta(500, {})) as unknown as typeof fetch;

    await expect(iniciarSesion('persona@example.com', 'x')).rejects.toThrow(
      'No se ha podido iniciar sesión. Inténtalo de nuevo.',
    );
  });

  it('si el servidor responde 200 con algo que no es JSON pide volver a intentarlo', async () => {
    globalThis.fetch = jest.fn(async () => ({
      ok: true,
      status: 200,
      json: async () => {
        throw new SyntaxError('JSON Parse error: Unexpected character: <');
      },
    })) as unknown as typeof fetch;

    await expect(iniciarSesion('persona@example.com', 'x')).rejects.toThrow(
      'No se ha podido iniciar sesión. Inténtalo de nuevo.',
    );
  });

  it('si el servidor responde 200 sin token pide volver a intentarlo', async () => {
    globalThis.fetch = jest.fn(async () =>
      respuesta(200, { tipo: 'Bearer' }),
    ) as unknown as typeof fetch;

    await expect(iniciarSesion('persona@example.com', 'x')).rejects.toThrow(
      'No se ha podido iniciar sesión. Inténtalo de nuevo.',
    );
  });

  it('sin conexión con el servidor pide volver a intentarlo', async () => {
    globalThis.fetch = jest.fn(async () => {
      throw new TypeError('Network request failed');
    }) as unknown as typeof fetch;

    await expect(iniciarSesion('persona@example.com', 'x')).rejects.toThrow(
      'No se ha podido conectar con el servidor. Comprueba tu conexión e inténtalo de nuevo.',
    );
  });
});

describe('token guardado', () => {
  beforeEach(() => __vaciar());

  it('guarda el token en el almacén cifrado del móvil y lo recupera', async () => {
    const token = tokenDePrueba(3600);

    await guardarToken(token);

    expect(await leerTokenGuardado()).toBe(token);
  });

  it('sin token guardado no hay sesión', async () => {
    expect(await leerTokenGuardado()).toBeNull();
  });

  it('un token caducado no cuenta como sesión y se borra', async () => {
    await guardarToken(tokenDePrueba(-60));

    expect(await leerTokenGuardado()).toBeNull();
    expect(SecureStore.deleteItemAsync).toHaveBeenCalledTimes(1);
  });

  it('un token ilegible no cuenta como sesión', async () => {
    await guardarToken('esto-no-es-un-jwt');

    expect(await leerTokenGuardado()).toBeNull();
  });

  it('si no se puede leer el almacén no hay sesión', async () => {
    (SecureStore.getItemAsync as jest.Mock).mockRejectedValueOnce(new Error('Could not decrypt'));

    expect(await leerTokenGuardado()).toBeNull();
  });

  it('cerrar sesión borra el token', async () => {
    await guardarToken(tokenDePrueba(3600));

    await borrarToken();

    expect(await leerTokenGuardado()).toBeNull();
  });
});

describe('mensajeParaElUsuario', () => {
  it('deja los mensajes que da el inicio de sesión', async () => {
    globalThis.fetch = jest.fn(async () => respuesta(401, {})) as unknown as typeof fetch;
    const error = await iniciarSesion('persona@example.com', 'mal').catch((e: unknown) => e);

    expect(mensajeParaElUsuario(error)).toBe('Correo o contraseña incorrectos');
  });

  it('cambia cualquier otro error, como uno del almacén del móvil, por un mensaje genérico', () => {
    expect(mensajeParaElUsuario(new Error('Invalid value provided to SecureStore'))).toBe(
      'No se ha podido iniciar sesión. Inténtalo de nuevo.',
    );
  });
});

describe('registrar', () => {
  const fetchOriginal = globalThis.fetch;
  afterEach(() => {
    globalThis.fetch = fetchOriginal;
  });

  const datos = {
    correo: 'persona@example.com',
    contrasena: 'contraseña-de-prueba',
    aceptaPrivacidad: true,
  };

  it('envía la cuenta nueva al servidor', async () => {
    const fetch = jest.fn(async () =>
      respuesta(201, { id: '00000000-0000-0000-0000-000000000000' }),
    );
    globalThis.fetch = fetch as unknown as typeof globalThis.fetch;

    await registrar(datos);

    expect(fetch).toHaveBeenCalledWith('http://localhost:8080/api/v1/usuarios', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(datos),
    });
  });

  it('si el correo ya tiene cuenta lo dice', async () => {
    globalThis.fetch = jest.fn(async () =>
      respuesta(409, { codigo: 'CORREO_YA_REGISTRADO', mensaje: 'El correo ya está registrado' }),
    ) as unknown as typeof fetch;

    await expect(registrar(datos)).rejects.toThrow('El correo ya está registrado');
  });

  it('si el servidor rechaza los datos muestra su mensaje', async () => {
    globalThis.fetch = jest.fn(async () =>
      respuesta(400, { codigo: 'DATOS_INVALIDOS', mensaje: 'La contraseña es demasiado larga' }),
    ) as unknown as typeof fetch;

    await expect(registrar(datos)).rejects.toThrow('La contraseña es demasiado larga');
  });

  it('si el servidor falla pide volver a intentarlo', async () => {
    globalThis.fetch = jest.fn(async () => respuesta(500, {})) as unknown as typeof fetch;

    const error = await registrar(datos).catch((e: unknown) => e);

    expect(mensajeParaElUsuario(error)).toBe(
      'No se ha podido crear la cuenta. Inténtalo de nuevo.',
    );
  });

  it('sin conexión con el servidor pide volver a intentarlo', async () => {
    globalThis.fetch = jest.fn(async () => {
      throw new TypeError('Network request failed');
    }) as unknown as typeof fetch;

    await expect(registrar(datos)).rejects.toThrow(
      'No se ha podido conectar con el servidor. Comprueba tu conexión e inténtalo de nuevo.',
    );
  });
});
