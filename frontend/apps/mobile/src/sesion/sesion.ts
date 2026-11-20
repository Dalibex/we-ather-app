import * as SecureStore from 'expo-secure-store';

import { URL_API } from '../config';

const CLAVE_TOKEN = 'token-sesion';

const ERROR_CREDENCIALES = 'Correo o contraseña incorrectos';
const ERROR_SERVIDOR = 'No se ha podido iniciar sesión. Inténtalo de nuevo.';
const ERROR_REGISTRO = 'No se ha podido crear la cuenta. Inténtalo de nuevo.';
const ERROR_CONEXION =
  'No se ha podido conectar con el servidor. Comprueba tu conexión e inténtalo de nuevo.';

/** Error del registro o del inicio de sesión con un mensaje ya escrito para el usuario. */
class ErrorConMensaje extends Error {}

/**
 * El mensaje que se enseña al usuario: el de los errores de `registrar` e `iniciarSesion` y, para
 * cualquier otro (por ejemplo, que el móvil no pueda guardar el token), uno genérico. Así nunca se
 * muestra un error interno ni en inglés.
 */
export function mensajeParaElUsuario(error: unknown): string {
  return error instanceof ErrorConMensaje ? error.message : ERROR_SERVIDOR;
}

/** Lo que pide `POST /api/v1/usuarios` para crear una cuenta. */
export interface CuentaNueva {
  correo: string;
  contrasena: string;
  aceptaPrivacidad: boolean;
}

/**
 * Crea la cuenta en el servidor (`POST /api/v1/usuarios`). Si no se puede, lanza un error con un
 * mensaje listo para mostrar al usuario: el del servidor si el correo ya tiene cuenta (409) o rechaza
 * los datos (400), o uno genérico.
 */
export async function registrar(cuenta: CuentaNueva): Promise<void> {
  const respuesta = await enviar('/api/v1/usuarios', cuenta);
  if (respuesta.ok) {
    return;
  }
  const cuerpo = await leerCuerpo(respuesta);
  if (
    (respuesta.status === 400 || respuesta.status === 409) &&
    typeof cuerpo?.mensaje === 'string'
  ) {
    throw new ErrorConMensaje(cuerpo.mensaje);
  }
  throw new ErrorConMensaje(ERROR_REGISTRO);
}

/**
 * Inicia sesión en el servidor (`POST /api/v1/sesiones`) y devuelve el token. Si no se puede, lanza
 * un error con un mensaje listo para mostrar al usuario (ver `mensajeParaElUsuario`).
 */
export async function iniciarSesion(correo: string, contrasena: string): Promise<string> {
  const respuesta = await enviar('/api/v1/sesiones', { correo, contrasena });
  if (respuesta.status === 401) {
    throw new ErrorConMensaje(ERROR_CREDENCIALES);
  }
  const cuerpo = await leerCuerpo(respuesta);
  if (respuesta.ok && typeof cuerpo?.token === 'string') {
    return cuerpo.token;
  }
  if (respuesta.status === 400 && typeof cuerpo?.mensaje === 'string') {
    throw new ErrorConMensaje(cuerpo.mensaje);
  }
  throw new ErrorConMensaje(ERROR_SERVIDOR);
}

/** Hace un POST con JSON al servidor; sin conexión, lanza el aviso de conexión. */
async function enviar(ruta: string, cuerpo: unknown): Promise<Response> {
  try {
    return await fetch(`${URL_API}${ruta}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(cuerpo),
    });
  } catch {
    throw new ErrorConMensaje(ERROR_CONEXION);
  }
}

/**
 * El cuerpo JSON de la respuesta, o `null` si no lo es (por ejemplo, la página de un portal wifi):
 * se trata como un fallo del servidor.
 */
async function leerCuerpo(
  respuesta: Response,
): Promise<{ token?: unknown; mensaje?: unknown } | null> {
  return respuesta.json().catch(() => null);
}

/** Guarda el token en el almacén cifrado del móvil (Keychain en iOS, Keystore en Android). */
export async function guardarToken(token: string): Promise<void> {
  await SecureStore.setItemAsync(CLAVE_TOKEN, token);
}

/**
 * El token de la sesión guardada, o `null` si no hay sesión. Un token caducado o ilegible se borra:
 * el servidor ya no lo aceptaría. Si el almacén no se puede leer (en Android pasa, por ejemplo, tras
 * restaurar una copia de seguridad), tampoco hay sesión.
 */
export async function leerTokenGuardado(): Promise<string | null> {
  const token = await SecureStore.getItemAsync(CLAVE_TOKEN).catch(() => null);
  if (token === null) {
    return null;
  }
  if (!sigueVigente(token)) {
    await borrarToken();
    return null;
  }
  return token;
}

/** Cierra la sesión: el token no se guarda en el servidor, así que basta con borrarlo aquí. */
export async function borrarToken(): Promise<void> {
  await SecureStore.deleteItemAsync(CLAVE_TOKEN);
}

/** Lee la caducidad (`exp`, en segundos) del contenido del JWT. La firma la comprueba el servidor. */
function sigueVigente(token: string): boolean {
  try {
    const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    // El JWT va sin el relleno «=» de Base64; se añade por si el atob del móvil lo exige.
    const contenido = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), '=');
    const { exp } = JSON.parse(atob(contenido)) as { exp?: unknown };
    return typeof exp === 'number' && exp * 1000 > Date.now();
  } catch {
    return false;
  }
}
