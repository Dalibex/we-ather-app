import type { ReactNode } from 'react';
import { View, type ViewProps } from 'react-native';

// Sustituto de react-native-maps para las pruebas: el mapa real es nativo y no existe en Jest.
// Cada componente se pinta como una View con las mismas props, para poder comprobarlas.
type Props = ViewProps & { children?: ReactNode } & Record<string, unknown>;

export default function MapView(props: Props) {
  return <View {...props} />;
}

export function UrlTile(props: Props) {
  return <View {...props} />;
}
