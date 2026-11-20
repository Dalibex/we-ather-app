// Sustituto de expo-secure-store para las pruebas: el almacén real es nativo (Keychain en iOS,
// Keystore en Android) y no existe en Jest. Guarda los valores en memoria.
const almacen = new Map<string, string>();

/** Vacía el almacén entre pruebas. */
export function __vaciar() {
  almacen.clear();
}

export const getItemAsync = jest.fn(async (clave: string) => almacen.get(clave) ?? null);

export const setItemAsync = jest.fn(async (clave: string, valor: string) => {
  almacen.set(clave, valor);
});

export const deleteItemAsync = jest.fn(async (clave: string) => {
  almacen.delete(clave);
});
