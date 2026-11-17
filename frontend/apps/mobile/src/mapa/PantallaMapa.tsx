import { Pressable, StyleSheet, Text, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { Buscador } from '../buscador/Buscador';
import { colores } from '../theme/colores';
import { MapaBase } from './MapaBase';

interface Props {
  /** Lleva al inicio de sesión a quien entró sin cuenta. */
  onIniciarSesion: () => void;
}

/**
 * Pantalla del mapa: el mapa base de Málaga, un botón pequeño para iniciar sesión y, debajo, el
 * buscador de origen y destino.
 */
export function PantallaMapa({ onIniciarSesion }: Props) {
  const margenes = useSafeAreaInsets();

  return (
    <View style={estilos.contenedor}>
      <MapaBase />
      <Pressable
        accessibilityRole="button"
        onPress={onIniciarSesion}
        style={[estilos.boton, { top: margenes.top + 8 }]}
        hitSlop={8}
      >
        <Text style={estilos.textoBoton}>Iniciar sesión</Text>
      </Pressable>
      <View style={[estilos.buscador, { top: margenes.top + 52 }]}>
        {/* Buscar las direcciones y calcular la ruta llega con T-07.2, T-07.3 y T-07.4. */}
        <Buscador onBuscar={() => {}} />
      </View>
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
  buscador: {
    left: 12,
    position: 'absolute',
    right: 12,
  },
  textoBoton: {
    color: colores.verde,
    fontSize: 13,
    fontWeight: '600',
  },
});
