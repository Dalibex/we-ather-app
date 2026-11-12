import { Linking, StyleSheet, Text, View } from 'react-native';
import MapView, { UrlTile, type Region } from 'react-native-maps';

import { URL_API } from '../config';

/** Vista inicial: el centro de Málaga, con unos 5 km de alto. */
const REGION_MALAGA: Region = {
  latitude: 36.7213,
  longitude: -4.4214,
  latitudeDelta: 0.05,
  longitudeDelta: 0.05,
};

// Las teselas son de OpenStreetMap, pero las sirve el servidor de WEather: OSM bloquea las
// peticiones directas de la app, que no puede identificarse como exige su política de uso.
const TESELAS_OSM = `${URL_API}/api/v1/teselas/{z}/{x}/{y}.png`;
/** Último nivel de zoom que sirve tile.openstreetmap.org. */
const ZOOM_MAXIMO_OSM = 19;
const LICENCIA_OSM = 'https://www.openstreetmap.org/copyright';

/**
 * Mapa base de Málaga con teselas de OpenStreetMap, que se desplaza y amplía con gestos.
 *
 * `mapType="none"` (Android) y `shouldReplaceMapContent` (iOS) ocultan el mapa del sistema para
 * que solo se vean las teselas de OSM. La atribución es obligatoria por su licencia.
 */
export function MapaBase() {
  return (
    <View style={styles.contenedor}>
      <MapView testID="mapa-base" style={styles.mapa} initialRegion={REGION_MALAGA} mapType="none">
        <UrlTile
          testID="teselas-osm"
          urlTemplate={TESELAS_OSM}
          maximumZ={ZOOM_MAXIMO_OSM}
          shouldReplaceMapContent
        />
      </MapView>
      <Text
        style={styles.atribucion}
        accessibilityRole="link"
        onPress={() => void Linking.openURL(LICENCIA_OSM)}
      >
        © OpenStreetMap contributors
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  contenedor: {
    flex: 1,
  },
  mapa: {
    flex: 1,
  },
  atribucion: {
    position: 'absolute',
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(255, 255, 255, 0.8)',
    color: '#0F172A',
    fontSize: 12,
    paddingHorizontal: 6,
    paddingVertical: 2,
  },
});
