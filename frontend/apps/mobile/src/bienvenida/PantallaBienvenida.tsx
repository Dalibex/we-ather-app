import { Pressable, StyleSheet, Text } from 'react-native';

import { MarcoAutenticacion } from '../components/MarcoAutenticacion';
import { colores } from '../theme/colores';
import { estilosFormulario as estilos } from '../theme/formulario';

interface Props {
  onIniciarSesion: () => void;
  onCrearCuenta: () => void;
  /** Buscar rutas no necesita cuenta: la cuenta solo hace falta para enviar reportes. */
  onContinuarSinCuenta: () => void;
}

/** Primera pantalla de la app: iniciar sesión, crear una cuenta o seguir sin cuenta hasta el mapa. */
export function PantallaBienvenida({
  onIniciarSesion,
  onCrearCuenta,
  onContinuarSinCuenta,
}: Props) {
  return (
    <MarcoAutenticacion
      titulo="Te damos la bienvenida"
      subtitulo="Rutas a pie por Málaga con menos calor"
    >
      <Pressable accessibilityRole="button" onPress={onIniciarSesion} style={estilos.boton}>
        <Text style={estilos.textoBoton}>Iniciar sesión</Text>
      </Pressable>

      <Pressable
        accessibilityRole="button"
        onPress={onCrearCuenta}
        style={[estilos.boton, estilosLocal.botonSecundario]}
      >
        <Text style={[estilos.textoBoton, estilosLocal.textoBotonSecundario]}>Crear cuenta</Text>
      </Pressable>

      <Pressable
        accessibilityRole="button"
        onPress={onContinuarSinCuenta}
        style={estilosLocal.sinCuenta}
        hitSlop={8}
      >
        <Text style={estilosLocal.textoSinCuenta}>Continuar sin cuenta</Text>
      </Pressable>
    </MarcoAutenticacion>
  );
}

/** Solo lo que es exclusivo de la bienvenida: el botón secundario y el enlace sin cuenta. */
const estilosLocal = StyleSheet.create({
  botonSecundario: {
    backgroundColor: colores.blanco,
    borderColor: colores.verde,
    borderWidth: 1,
    marginTop: 12,
  },
  textoBotonSecundario: {
    color: colores.verde,
  },
  sinCuenta: {
    alignSelf: 'center',
    marginTop: 20,
    // Con el hitSlop, la zona táctil llega a unos 48 dp.
    paddingVertical: 8,
  },
  textoSinCuenta: {
    color: colores.textoSecundario,
    fontSize: 14,
    textDecorationLine: 'underline',
  },
});
