import { StatusBar } from 'expo-status-bar';
import { useState } from 'react';
import { SafeAreaProvider } from 'react-native-safe-area-context';

import { PantallaBienvenida } from './src/bienvenida/PantallaBienvenida';
import { PantallaInicioSesion } from './src/login/PantallaInicioSesion';
import { PantallaMapa } from './src/mapa/PantallaMapa';
import { PantallaRegistro } from './src/registro/PantallaRegistro';

type Pantalla = 'bienvenida' | 'mapa' | 'registro' | 'login';
/** Pantallas desde las que se entra al registro o al inicio de sesión, y a las que vuelve «Volver». */
type Origen = 'bienvenida' | 'mapa';

/**
 * Raíz de la app. Empieza en la bienvenida, desde la que se inicia sesión, se crea una cuenta o se
 * sigue sin cuenta hasta el mapa. Desde el mapa también se puede iniciar sesión.
 */
export default function App() {
  const [pantalla, setPantalla] = useState<Pantalla>('bienvenida');
  const [origen, setOrigen] = useState<Origen>('bienvenida');

  const abrirAcceso = (destino: 'registro' | 'login', desde: Origen) => {
    setOrigen(desde);
    setPantalla(destino);
  };

  return (
    <SafeAreaProvider>
      {pantalla === 'registro' ? (
        <PantallaRegistro
          // Validar y enviar los datos llega con T-01.2, T-01.3 y T-02.3. Nunca se escriben en la consola.
          onEnviar={() => {}}
          onVolver={() => setPantalla(origen)}
          onIniciarSesion={() => setPantalla('login')}
        />
      ) : pantalla === 'login' ? (
        <PantallaInicioSesion
          onEnviar={() => {}}
          onVolver={() => setPantalla(origen)}
          onIrRegistro={() => setPantalla('registro')}
        />
      ) : pantalla === 'mapa' ? (
        <PantallaMapa onIniciarSesion={() => abrirAcceso('login', 'mapa')} />
      ) : (
        <PantallaBienvenida
          onIniciarSesion={() => abrirAcceso('login', 'bienvenida')}
          onCrearCuenta={() => abrirAcceso('registro', 'bienvenida')}
          onContinuarSinCuenta={() => setPantalla('mapa')}
        />
      )}
      <StatusBar style="auto" />
    </SafeAreaProvider>
  );
}
