import { Component, type ReactNode } from 'react';
import { View, type ViewProps } from 'react-native';

// Sustituto de react-native-maps para las pruebas: el mapa real es nativo y no existe en Jest.
// Cada componente se pinta como una View con las mismas props, para poder comprobarlas.
type Props = ViewProps & { children?: ReactNode } & Record<string, unknown>;

/** Los movimientos que la app pide al mapa, para comprobarlos en las pruebas. */
export const animateToRegion = jest.fn();

export default class MapView extends Component<Props> {
  animateToRegion = animateToRegion;

  render() {
    return <View {...this.props} />;
  }
}

export function UrlTile(props: Props) {
  return <View {...props} />;
}
