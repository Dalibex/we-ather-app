import { Linking, StyleSheet, Text, View } from 'react-native';
import MapView, { UrlTile, type Region } from 'react-native-maps';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

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
 * que solo se vean las teselas de OSM. La atribución es obligatoria por su licencia: se coloca,
 * igual que el logo del mapa del sistema, por encima de los botones de navegación del móvil, para
 * que se lea y se pueda pulsar.
 */
export function MapaBase() {
  const margenes = useSafeAreaInsets();

  return (
    <View style={styles.contenedor}>
      <MapView
        testID="mapa-base"
        style={styles.mapa}
        initialRegion={REGION_MALAGA}
        mapType="none"
        mapPadding={{ top: margenes.top, right: 0, bottom: margenes.bottom, left: 0 }}
      >
        <UrlTile
          testID="teselas-osm"
          urlTemplate={TESELAS_OSM}
          maximumZ={ZOOM_MAXIMO_OSM}
          shouldReplaceMapContent
        />
      </MapView>
      <Text
        style={[styles.atribucion, { bottom: margenes.bottom }]}
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
    backgroundColor: 'rgba(255, 255, 255, 0.9)',
    color: '#0F172A',
    fontSize: 13,
    paddingHorizontal: 8,
    paddingVertical: 6,
  },
});
