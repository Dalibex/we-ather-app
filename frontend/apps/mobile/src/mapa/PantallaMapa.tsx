import { Pressable, StyleSheet, Text, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { colores } from '../theme/colores';
import { MapaBase } from './MapaBase';

interface Props {
  /** Lleva al inicio de sesión a quien entró sin cuenta. */
  onIniciarSesion: () => void;
  /**
   * Cierra la sesión iniciada. Solo se pasa si hay sesión: entonces el botón sirve para cerrarla en
   * lugar de para iniciarla.
   */
  onCerrarSesion?: () => void;
}

/** Pantalla del mapa: el mapa base de Málaga y un botón pequeño para iniciar o cerrar sesión. */
export function PantallaMapa({ onIniciarSesion, onCerrarSesion }: Props) {
  const margenes = useSafeAreaInsets();
  const sesionIniciada = onCerrarSesion !== undefined;

  return (
    <View style={estilos.contenedor}>
      <MapaBase />
      <Pressable
        accessibilityRole="button"
        onPress={onCerrarSesion ?? onIniciarSesion}
        style={[estilos.boton, { top: margenes.top + 8 }]}
        hitSlop={8}
      >
        <Text style={estilos.textoBoton}>
          {sesionIniciada ? 'Cerrar sesión' : 'Iniciar sesión'}
        </Text>
      </Pressable>
    </View>
  );
}

const estilos = StyleSheet.create({
  contenedor: {
    flex: 1,
  },
  boton: {
    backgroundColor: colores.tarjeta,
    borderColor: colores.borde,
    borderRadius: 16,
    borderWidth: 1,
    paddingHorizontal: 12,
    paddingVertical: 6,
    position: 'absolute',
    // A la izquierda: arriba a la derecha va la brújula del mapa en iOS, y abajo, la atribución.
    left: 12,
  },
  textoBoton: {
    color: colores.verde,
    fontSize: 13,
    fontWeight: '600',
  },
});
