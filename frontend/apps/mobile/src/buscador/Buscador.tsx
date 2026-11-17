import { useState } from 'react';
import { Pressable, StyleSheet, Text, TextInput, View } from 'react-native';

import { colores } from '../theme/colores';
import { estilosFormulario } from '../theme/formulario';

/** Origen y destino que ha escrito el usuario, sin espacios al principio ni al final. */
export interface Busqueda {
  origen: string;
  destino: string;
}

interface Props {
  /** Recibe el origen y el destino al pulsar «Buscar ruta». Buscarlos no es cosa de esta vista. */
  onBuscar: (busqueda: Busqueda) => void;
}

/**
 * Buscador de la ruta (HU-07): el origen y el destino se escriben, sin GPS ni ubicación. Debajo de
 * cada campo queda el hueco donde irán sus coincidencias (T-07.4). «Buscar ruta» solo se activa con
 * los dos campos rellenos.
 */
export function Buscador({ onBuscar }: Props) {
  const [origen, setOrigen] = useState('');
  const [destino, setDestino] = useState('');
  const busqueda: Busqueda = { origen: origen.trim(), destino: destino.trim() };
  const completa = busqueda.origen !== '' && busqueda.destino !== '';

  return (
    <View style={estilos.panel}>
      <TextInput
        accessibilityLabel="Origen"
        value={origen}
        onChangeText={setOrigen}
        placeholder="Origen"
        placeholderTextColor={colores.placeholder}
        returnKeyType="next"
        style={[estilosFormulario.campo, estilos.campo]}
      />
      <View testID="coincidencias-origen" />

      <TextInput
        accessibilityLabel="Destino"
        value={destino}
        onChangeText={setDestino}
        placeholder="Destino"
        placeholderTextColor={colores.placeholder}
        returnKeyType="search"
        style={[estilosFormulario.campo, estilos.campo]}
      />
      <View testID="coincidencias-destino" />

      <Pressable
        accessibilityRole="button"
        accessibilityState={{ disabled: !completa }}
        disabled={!completa}
        onPress={() => onBuscar(busqueda)}
        style={[estilosFormulario.boton, estilos.boton, !completa && estilos.botonDesactivado]}
      >
        <Text style={estilosFormulario.textoBoton}>Buscar ruta</Text>
      </Pressable>
    </View>
  );
}

const estilos = StyleSheet.create({
  panel: {
    backgroundColor: colores.tarjeta,
    borderColor: colores.borde,
    borderRadius: 20,
    borderWidth: 1,
    padding: 12,
  },
  campo: {
    marginBottom: 8,
    paddingVertical: 10,
  },
  boton: {
    marginTop: 0,
    paddingVertical: 12,
  },
  botonDesactivado: {
    opacity: 0.4,
  },
});
