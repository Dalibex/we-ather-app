/**
 * Solo para las pruebas: un JWT con la caducidad indicada. La firma no importa, porque la app no la
 * comprueba (eso lo hace el servidor); solo lee cuándo caduca.
 */
export function tokenDePrueba(segundosHastaCaducar: number): string {
  const base64url = (texto: string) =>
    btoa(texto).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
  const exp = Math.floor(Date.now() / 1000) + segundosHastaCaducar;
  return [
    base64url(JSON.stringify({ alg: 'HS256' })),
    base64url(JSON.stringify({ sub: '00000000-0000-0000-0000-000000000000', exp })),
    'firma',
  ].join('.');
}
