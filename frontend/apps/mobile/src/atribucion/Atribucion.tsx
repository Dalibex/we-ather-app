import { Linking, StyleSheet, Text, View } from 'react-native';

/**
 * Proveedores de datos abiertos que usa WEather, con el texto que cada uno pide mostrar y el enlace
 * a su licencia o, si no tiene una página propia, a su web.
 */
const PROVEEDORES = [
  { texto: '© OpenStreetMap contributors', enlace: 'https://www.openstreetmap.org/copyright' },
  { texto: 'Open-Meteo.com', enlace: 'https://open-meteo.com/' },
  { texto: 'Fuente: AEMET', enlace: 'https://www.aemet.es/es/nota_legal' },
  { texto: 'Ayuntamiento de Málaga', enlace: 'https://datosabiertos.malaga.eu/' },
] as const;

/** Atribución obligatoria de los proveedores de datos, sobre el mapa. Cada nombre abre su enlace. */
export function Atribucion({ margenInferior = 0 }: { margenInferior?: number }) {
  return (
    <View testID="atribucion" style={[styles.contenedor, { bottom: margenInferior }]}>
      {PROVEEDORES.map(({ texto, enlace }) => (
        <Text
          key={texto}
          style={styles.proveedor}
          accessibilityRole="link"
          onPress={() => void Linking.openURL(enlace)}
        >
          {texto}
        </Text>
      ))}
    </View>
  );
}

const styles = StyleSheet.create({
  contenedor: {
    position: 'absolute',
    right: 0,
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'flex-end',
    columnGap: 6,
    backgroundColor: 'rgba(255, 255, 255, 0.9)',
    paddingHorizontal: 8,
  },
  proveedor: {
    color: '#0F172A',
    fontSize: 13,
    // Zona táctil más alta para pulsar el enlace con el dedo.
    paddingVertical: 8,
  },
});
