import { Image, StyleSheet, View } from 'react-native';
import iconoBiznaga from '../../assets/icono-biznaga-malaga.png';

/** Patrón decorativo de biznagas para el fondo. */
export function PatronBiznagas() {
  return (
    <View pointerEvents="none" style={estilos.patron}>
      {Array.from({ length: 18 }, (_, indice) => (
        <View key={indice} style={estilos.baldosa}>
          <Image
            source={iconoBiznaga}
            style={[estilos.iconoBiznaga, { transform: [{ rotate: `${(indice % 4) * 20}deg` }] }]}
            resizeMode="contain"
          />
        </View>
      ))}
    </View>
  );
}

const estilos = StyleSheet.create({
  patron: {
    ...StyleSheet.absoluteFill,
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-around',
    alignContent: 'space-around',
  },
  baldosa: {
    width: '33.33%',
    height: '20%',
    justifyContent: 'center',
    alignItems: 'center',
  },
  iconoBiznaga: {
    width: 42,
    height: 42,
    opacity: 0.1,
  },
});
