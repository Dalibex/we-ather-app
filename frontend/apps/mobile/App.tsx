import { StatusBar } from 'expo-status-bar';
import { useEffect, useRef, useState } from 'react';
import { BackHandler } from 'react-native';
import { SafeAreaProvider } from 'react-native-safe-area-context';

import { PantallaBienvenida } from './src/bienvenida/PantallaBienvenida';
import { PantallaInicioSesion } from './src/login/PantallaInicioSesion';
import { PantallaMapa } from './src/mapa/PantallaMapa';
import { PantallaRegistro } from './src/registro/PantallaRegistro';
import type { DatosInicioSesion } from './src/login/PantallaInicioSesion';
import {
  borrarToken,
  guardarToken,
  iniciarSesion,
  leerTokenGuardado,
  mensajeParaElUsuario,
  registrar,
} from './src/sesion/sesion';
import type { DatosRegistro } from './src/registro/PantallaRegistro';

const AVISO_CUENTA_CREADA = 'Cuenta creada. Ya puedes iniciar sesión.';

type Pantalla = 'bienvenida' | 'mapa' | 'registro' | 'login';
/** Pantallas desde las que se entra al registro o al inicio de sesión, y a las que vuelve «Volver». */
type Origen = 'bienvenida' | 'mapa';

/**
 * Raíz de la app. Empieza en la bienvenida, desde la que se inicia sesión, se crea una cuenta o se
 * sigue sin cuenta hasta el mapa. Desde el mapa también se puede iniciar sesión, o cerrarla. Si hay
 * una sesión guardada, abre directamente en el mapa.
 */
export default function App() {
  const [pantalla, setPantalla] = useState<Pantalla>('bienvenida');
  // La pantalla de ahora, para que una respuesta que llega tarde no saque al usuario de donde está.
  const pantallaActual = useRef(pantalla);
  useEffect(() => {
    pantallaActual.current = pantalla;
  }, [pantalla]);
  const [origen, setOrigen] = useState<Origen>('bienvenida');
  // Solo el correo pasa del inicio de sesión al registro y al revés; la contraseña, nunca.
  const [correo, setCorreo] = useState('');
  // Token de la sesión iniciada (HU-02). Lo necesitarán las peticiones que piden estar identificado.
  const [token, setToken] = useState<string | null>(null);
  const [errorInicioSesion, setErrorInicioSesion] = useState<string>();
  const [errorRegistro, setErrorRegistro] = useState<string>();
  const [avisoInicioSesion, setAvisoInicioSesion] = useState<string>();
  const [enviando, setEnviando] = useState(false);

  // Recupera la sesión guardada al abrir la app.
  useEffect(() => {
    let vigente = true;
    leerTokenGuardado().then((guardado) => {
      if (vigente && guardado !== null) {
        setToken(guardado);
        setPantalla((actual) => (actual === 'bienvenida' ? 'mapa' : actual));
      }
    });
    return () => {
      vigente = false;
    };
  }, []);

  // En Android, el botón o gesto «atrás» vuelve a la pantalla anterior: del registro o el inicio de
  // sesión, a la de origen; del mapa, a la bienvenida. En la bienvenida, o en el mapa con la sesión
  // iniciada, no se intercepta y el sistema cierra la app, como siempre.
  useEffect(() => {
    if (pantalla === 'bienvenida' || (pantalla === 'mapa' && token !== null)) {
      return;
    }
    const anterior: Pantalla = pantalla === 'mapa' ? 'bienvenida' : origen;
    const suscripcion = BackHandler.addEventListener('hardwareBackPress', () => {
      setPantalla(anterior);
      return true;
    });
    return () => suscripcion.remove();
  }, [pantalla, origen, token]);

  // Al entrar en el registro o en el inicio de sesión no se arrastran los mensajes de antes.
  const irA = (destino: Pantalla) => {
    setErrorInicioSesion(undefined);
    setErrorRegistro(undefined);
    setAvisoInicioSesion(undefined);
    setPantalla(destino);
  };

  const abrirAcceso = (destino: 'registro' | 'login', desde: Origen) => {
    setOrigen(desde);
    irA(destino);
  };

  const enviarInicioSesion = async ({ correo, contrasena }: DatosInicioSesion) => {
    setEnviando(true);
    setErrorInicioSesion(undefined);
    try {
      const nuevo = await iniciarSesion(correo, contrasena);
      await guardarToken(nuevo);
      setToken(nuevo);
      // Solo se lleva al mapa a quien sigue en el inicio de sesión, no a quien se fue mientras esperaba.
      if (pantallaActual.current === 'login') {
        setPantalla('mapa');
      }
    } catch (error) {
      setErrorInicioSesion(mensajeParaElUsuario(error));
    } finally {
      setEnviando(false);
    }
  };

  const enviarRegistro = async ({ correo, contrasena, confirmaEdadYPolitica }: DatosRegistro) => {
    setEnviando(true);
    setErrorRegistro(undefined);
    try {
      await registrar({ correo, contrasena, aceptaPrivacidad: confirmaEdadYPolitica });
      // Solo se lleva al inicio de sesión a quien sigue en el registro.
      if (pantallaActual.current === 'registro') {
        irA('login');
        setCorreo(correo);
        setAvisoInicioSesion(AVISO_CUENTA_CREADA);
      }
    } catch (error) {
      setErrorRegistro(mensajeParaElUsuario(error));
    } finally {
      setEnviando(false);
    }
  };

  // La sesión se cierra en la app aunque el móvil no consiga borrar el token guardado.
  const cerrarSesion = async () => {
    setToken(null);
    await borrarToken().catch(() => {});
  };

  return (
    <SafeAreaProvider>
      {pantalla === 'registro' ? (
        <PantallaRegistro
          // Los datos nunca se escriben en la consola.
          onEnviar={enviarRegistro}
          error={errorRegistro}
          enviando={enviando}
          onVolver={() => irA(origen)}
          onIniciarSesion={() => irA('login')}
          correoInicial={correo}
          onCambiarCorreo={setCorreo}
        />
      ) : pantalla === 'login' ? (
        <PantallaInicioSesion
          onEnviar={enviarInicioSesion}
          error={errorInicioSesion}
          enviando={enviando}
          aviso={avisoInicioSesion}
          onVolver={() => irA(origen)}
          onIrRegistro={() => irA('registro')}
          correoInicial={correo}
          onCambiarCorreo={setCorreo}
        />
      ) : pantalla === 'mapa' ? (
        <PantallaMapa
          onIniciarSesion={() => abrirAcceso('login', 'mapa')}
          onCerrarSesion={token !== null ? cerrarSesion : undefined}
        />
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
