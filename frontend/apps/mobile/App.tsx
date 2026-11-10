import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View } from 'react-native';

/** Pantalla inicial del esqueleto: comprueba que la app arranca. */
export default function App() {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>WEather</Text>
      <Text style={styles.subtitle}>Rutas a pie por Málaga con menos calor.</Text>
      <StatusBar style="auto" />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#FFFFFF',
    alignItems: 'center',
    justifyContent: 'center',
    padding: 16,
  },
  title: {
    color: '#0F172A',
    fontSize: 24,
    fontWeight: 'bold',
  },
  subtitle: {
    color: '#64748B',
    fontSize: 16,
    marginTop: 8,
  },
});
