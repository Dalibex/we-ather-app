import { Image, StyleSheet, View } from 'react-native';
import iconoBiznaga from '../../assets/icono-biznaga-malaga.png';

/** Patrón decorativo de biznagas para el fondo. */
export function PatronBiznagas() {
  return (
    <View pointerEvents="none" style={styles.patron}>
      {Array.from({ length: 18 }, (_, index) => (
        <View key={index} style={styles.baldosa}>
          <Image
            source={iconoBiznaga}
            style={[styles.iconoBiznaga, { transform: [{ rotate: `${(index % 4) * 20}deg` }] }]}
            resizeMode="contain"
          />
        </View>
      ))}
    </View>
  );
}

const styles = StyleSheet.create({
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
