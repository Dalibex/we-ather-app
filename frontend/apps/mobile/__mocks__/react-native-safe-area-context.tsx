import type { ReactNode } from 'react';

// Sustituto de react-native-safe-area-context para las pruebas: simula un móvil con barra de
// estado arriba y botones de navegación del sistema abajo.
export const MARGENES_DE_PRUEBA = { top: 24, right: 0, bottom: 48, left: 0 };

export function SafeAreaProvider({ children }: { children?: ReactNode }) {
  return <>{children}</>;
}

export function useSafeAreaInsets() {
  return MARGENES_DE_PRUEBA;
}
