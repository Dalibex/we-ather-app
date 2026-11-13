import { StatusBar } from 'expo-status-bar';
import { SafeAreaProvider } from 'react-native-safe-area-context';

import { MapaBase } from './src/mapa/MapaBase';

/** Pantalla principal: el mapa base de Málaga. */
export default function App() {
  return (
    <SafeAreaProvider>
      <MapaBase />
      <StatusBar style="auto" />
    </SafeAreaProvider>
  );
}
