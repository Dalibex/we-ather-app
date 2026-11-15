/**
 * Dirección del servidor de WEather. Se configura con EXPO_PUBLIC_API_URL en `.env` (ver
 * `.env.example`); en un móvil real `localhost` es el propio móvil, así que hay que poner la IP del
 * ordenador donde corre el servidor.
 */
export const URL_API = process.env.EXPO_PUBLIC_API_URL ?? 'http://localhost:8080';
