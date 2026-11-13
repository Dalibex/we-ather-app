import { StatusBar } from 'expo-status-bar';
import { useState } from 'react';
import { Pressable, StyleSheet, Text, View } from 'react-native';

import { PantallaRegistro } from './src/registro/PantallaRegistro';
import { PantallaInicioSesion } from './src/login/PantallaInicioSesion';

type Pantalla = 'inicio' | 'registro' | 'login';

/** Raíz de la app: la pantalla inicial y, desde ella, la de registro. */
export default function App() {
  const [pantalla, setPantalla] = useState<Pantalla>('inicio');

  return (
    <>
      {pantalla === 'registro' ? (
        <PantallaRegistro
          onEnviar={(datos) => console.log('Registro:', datos)}
          onVolver={() => setPantalla('inicio')}
          onIniciarSesion={() => setPantalla('login')}
        />
      ) : pantalla === 'login' ? (
        <PantallaInicioSesion
          onEnviar={(datos) => console.log('Inicio de sesión:', datos)}
          onVolver={() => setPantalla('inicio')}
          onIrRegistro={() => setPantalla('registro')}
        />
      ) : (
        <View style={styles.container}>
          <Text style={styles.title}>WEather</Text>

          <Text style={styles.subtitle}>Rutas a pie por Málaga con menos calor.</Text>

          <Pressable
            accessibilityRole="button"
            onPress={() => setPantalla('login')}
            style={styles.loginButton}
          >
            <Text style={styles.buttonText}>Iniciar sesión</Text>
          </Pressable>

          <Pressable
            accessibilityRole="button"
            onPress={() => setPantalla('registro')}
            style={styles.registerButton}
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
    textAlign: 'center',
  },
  loginButton: {
    backgroundColor: '#2563EB',
    borderRadius: 8,
    marginTop: 24,
    paddingHorizontal: 24,
    paddingVertical: 12,
    width: '100%',
    alignItems: 'center',
  },
  registerButton: {
    backgroundColor: '#64748B',
    borderRadius: 8,
    marginTop: 12,
    paddingHorizontal: 24,
    paddingVertical: 12,
    width: '100%',
    alignItems: 'center',
  },
  buttonText: {
    color: '#FFFFFF',
    fontSize: 16,
    fontWeight: 'bold',
  },
});
