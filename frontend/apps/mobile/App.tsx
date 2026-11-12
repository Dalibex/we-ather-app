import { StatusBar } from 'expo-status-bar';
import { useState } from 'react';
import { Pressable, StyleSheet, Text, View } from 'react-native';

import { PantallaRegistro } from './src/registro/PantallaRegistro';

type Pantalla = 'inicio' | 'registro';

/** Raíz de la app: la pantalla inicial y, desde ella, la de registro. */
export default function App() {
  const [pantalla, setPantalla] = useState<Pantalla>('inicio');

  return (
    <>
      {pantalla === 'registro' ? (
        // Validar los datos (T-01.3) y enviarlos al servidor (T-01.2) llega en otras tareas.
        <PantallaRegistro onEnviar={() => {}} onVolver={() => setPantalla('inicio')} />
      ) : (
        <View style={styles.container}>
          <Text style={styles.title}>WEather</Text>
          <Text style={styles.subtitle}>Rutas a pie por Málaga con menos calor.</Text>
          <Pressable
            accessibilityRole="button"
            onPress={() => setPantalla('registro')}
            style={styles.button}
          >
            <Text style={styles.buttonText}>Crear cuenta</Text>
          </Pressable>
        </View>
      )}
      <StatusBar style="auto" />
    </>
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
  button: {
    backgroundColor: '#2563EB',
    borderRadius: 8,
    marginTop: 24,
    paddingHorizontal: 24,
    paddingVertical: 12,
  },
  buttonText: {
    color: '#FFFFFF',
    fontSize: 16,
    fontWeight: 'bold',
  },
});
