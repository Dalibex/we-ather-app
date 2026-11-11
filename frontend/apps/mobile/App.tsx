import { StatusBar } from 'expo-status-bar';

import { MapaBase } from './src/mapa/MapaBase';

/** Pantalla principal: el mapa base de Málaga. */
export default function App() {
  return (
    <>
      <MapaBase />
      <StatusBar style="auto" />
    </>
  );
}
