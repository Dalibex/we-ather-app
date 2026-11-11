import { Linking, StyleSheet, Text, View } from 'react-native';

/**
 * Proveedores de datos abiertos que usa WEather, con el texto y el enlace a la licencia que cada
 * uno pide mostrar.
 */
const PROVEEDORES = [
  { texto: '© OpenStreetMap contributors', licencia: 'https://www.openstreetmap.org/copyright' },
  { texto: 'Open-Meteo.com', licencia: 'https://open-meteo.com/' },
  { texto: '© AEMET', licencia: 'https://www.aemet.es/es/nota_legal' },
  { texto: 'Ayuntamiento de Málaga', licencia: 'https://datosabiertos.malaga.eu/' },
] as const;

/** Atribución obligatoria de los proveedores de datos, sobre el mapa. Cada nombre abre su licencia. */
export function Atribucion({ margenInferior = 0 }: { margenInferior?: number }) {
  return (
    <View testID="atribucion" style={[styles.contenedor, { bottom: margenInferior }]}>
      {PROVEEDORES.map(({ texto, licencia }) => (
        <Text
          key={texto}
          style={styles.proveedor}
          accessibilityRole="link"
          onPress={() => void Linking.openURL(licencia)}
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
    paddingVertical: 2,
  },
  proveedor: {
    color: '#0F172A',
    fontSize: 13,
  },
});
