
import { useEffect, useRef, useState } from 'react';
import {
  Animated,
  Keyboard,
  Linking,
  Pressable,
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';
import { PatronBiznagas } from '../components/PatronBiznagas';
import { CabeceraLogo } from '../components/CabeceraLogo';
import { estilosFormulario as estilos } from '../theme/formulario';

/** La política vive en docs/privacidad.md; la app abre la versión publicada en main. */
const URL_POLITICA_PRIVACIDAD =
  'https://github.com/Dalibex/we-ather-app/blob/main/docs/privacidad.md';

/** Lo que el usuario ha escrito en el formulario de registro, tal cual, sin validar. */
export interface DatosRegistro {
  correo: string;
  contrasena: string;
  privacidadAceptada: boolean;
}

interface Props {
  onEnviar: (datos: DatosRegistro) => void;
  onVolver: () => void;
  onIniciarSesion: () => void;
}

/** Pantalla de registro (HU-01): correo, contraseña y aceptación de la política de privacidad. */
export function PantallaRegistro({ onEnviar, onVolver }: Props) {
  const [correo, setCorreo] = useState('');
  const [contrasena, setContrasena] = useState('');
  const [privacidadAceptada, setPrivacidadAceptada] = useState(false);
  const [tecladoVisible, setTecladoVisible] = useState(false);

  const desplazamiento = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    const mostrar = Keyboard.addListener('keyboardDidShow', () => {
      setTecladoVisible(true);
    });

    const ocultar = Keyboard.addListener('keyboardDidHide', () => {
      setTecladoVisible(false);
    });

    return () => {
      mostrar.remove();
      ocultar.remove();
    };
  }, []);

  useEffect(() => {
    Animated.timing(desplazamiento, {
      toValue: tecladoVisible ? 1 : 0,
      duration: 500,
      useNativeDriver: false,
    }).start();
  }, [tecladoVisible, desplazamiento]);

  const paddingTop = desplazamiento.interpolate({
    inputRange: [0, 1],
    outputRange: [0, 8],
  });

  const posicion = desplazamiento.interpolate({
    inputRange: [0, 1],
    outputRange: [0, -100],
  });

  return (
    <Animated.View
      style={[
        estilos.container,
        {
          paddingTop,
          transform: [{ translateY: posicion }],
        },
      ]}
    >
      <PatronBiznagas />

      <Pressable
        accessibilityRole="button"
        accessibilityLabel="Volver"
        onPress={onVolver}
        style={estilos.volver}
      >
        <Text style={estilos.enlace}>← Volver</Text>
      </Pressable>

      <View style={estilos.formulario}>
        <CabeceraLogo />

        <Text style={estilos.titulo}>Crear cuenta</Text>

        <Text style={estilos.subtitulo}>
          Únete para pasear sin sorpresas de calor
        </Text>

        <Text style={estilos.etiqueta}>Correo electrónico</Text>
        <TextInput
          accessibilityLabel="Correo electrónico"
          value={correo}
          onChangeText={setCorreo}
          keyboardType="email-address"
          autoCapitalize="none"
          autoComplete="email"
          autoCorrect={false}
          placeholder="tu@email.com"
          placeholderTextColor="#94A3A0"
          style={estilos.campo}
        />

        <Text style={estilos.etiqueta}>Contraseña</Text>
        <TextInput
          accessibilityLabel="Contraseña"
          value={contrasena}
          onChangeText={setContrasena}
          secureTextEntry
          autoCapitalize="none"
          autoComplete="new-password"
          placeholder="Tu contraseña"
          placeholderTextColor="#94A3A0"
          style={estilos.campo}
        />

        <Pressable
          accessibilityRole="checkbox"
          accessibilityState={{ checked: privacidadAceptada }}
          onPress={() => setPrivacidadAceptada((aceptada) => !aceptada)}
          style={estilosLocal.casilla}
        >
          <View
            style={[
              estilosLocal.cuadro,
              privacidadAceptada && estilosLocal.cuadroMarcado,
            ]}
          >
            {privacidadAceptada && <Text style={estilosLocal.marca}>✓</Text>}
          </View>
          <Text style={estilosLocal.textoCasilla}>
            Tengo 14 años o más y acepto la política de privacidad
          </Text>
        </Pressable>

        <Pressable
          accessibilityRole="link"
          onPress={() => Linking.openURL(URL_POLITICA_PRIVACIDAD)}
          style={estilosLocal.enlacePolitica}
          hitSlop={8}
        >
          <Text style={estilosLocal.enlacePoliticaTexto}>
            Leer la política de privacidad
          </Text>
        </Pressable>

        <Pressable
          accessibilityRole="button"
          onPress={() => onEnviar({ correo, contrasena, privacidadAceptada })}
          style={estilos.boton}
        >
          <Text style={estilos.textoBoton}>Crear cuenta</Text>
        </Pressable>
      </View>

      <Text style={estilos.pie}>PARA AGUANTAR LA CALOR</Text>
    </Animated.View>
  );
}

/** Solo lo que es exclusivo del registro (casilla y enlace a la política). */
const estilosLocal = StyleSheet.create({
  casilla: {
    alignItems: 'center',
    flexDirection: 'row',
    marginBottom: 8,
    paddingVertical: 4,
  },
  cuadro: {
    alignItems: 'center',
    borderColor: '#718078',
    borderRadius: 4,
    borderWidth: 1,
    height: 22,
    justifyContent: 'center',
    marginRight: 10,
    width: 22,
  },
  cuadroMarcado: {
    backgroundColor: '#304B43',
    borderColor: '#304B43',
  },
  marca: {
    color: '#FFFFFF',
    fontSize: 14,
    fontWeight: 'bold',
  },
  textoCasilla: {
    color: '#304B43',
    flexShrink: 1,
    fontSize: 13,
    lineHeight: 18,
  },
  enlacePolitica: {
    alignSelf: 'flex-start',
    marginBottom: 24,
    paddingVertical: 4,
  },
  enlacePoliticaTexto: {
    color: '#304B43',
    fontSize: 13,
    fontWeight: 'bold',
    textDecorationLine: 'underline',
  },
});
