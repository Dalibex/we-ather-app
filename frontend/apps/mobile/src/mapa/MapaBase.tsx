import { useRef } from 'react';
import { StyleSheet, View } from 'react-native';
import MapView, { UrlTile, type Region } from 'react-native-maps';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { Atribucion } from '../atribucion/Atribucion';
import { URL_API } from '../config';
import { ZOOM_MINIMO_MALAGA, ajustarAMalaga } from './limiteMalaga';

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

/**
 * Mapa base de Málaga con teselas de OpenStreetMap, que se desplaza y amplía con gestos.
 *
 * `mapType="none"` (Android) y `shouldReplaceMapContent` (iOS) ocultan el mapa del sistema para
 * que solo se vean las teselas de OSM. Encima va la atribución de los proveedores de datos, que se
 * coloca, igual que el logo del mapa del sistema, por encima de los botones de navegación del móvil
 * para que se lea y se pueda pulsar.
 *
 * La navegación se limita a Málaga: no se puede alejar más allá de lo que ocupa la ciudad y, si se
 * arrastra fuera, el mapa vuelve al borde de Málaga al soltarlo.
 */
export function MapaBase() {
  const margenes = useSafeAreaInsets();
  const mapa = useRef<MapView>(null);

  function mantenerEnMalaga(region: Region) {
    const ajustada = ajustarAMalaga(region);
    if (ajustada !== region) {
      mapa.current?.animateToRegion(ajustada);
    }
  }

  return (
    <View style={styles.contenedor}>
      <MapView
        ref={mapa}
        testID="mapa-base"
        style={styles.mapa}
        initialRegion={REGION_MALAGA}
        minZoomLevel={ZOOM_MINIMO_MALAGA}
        onRegionChangeComplete={mantenerEnMalaga}
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
      <Atribucion margenInferior={margenes.bottom} />
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
});
