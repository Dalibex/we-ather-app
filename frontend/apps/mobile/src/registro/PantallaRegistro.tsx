import { useState } from 'react';
import { Linking, Pressable, StyleSheet, Text, TextInput, View } from 'react-native';

import { MarcoAutenticacion } from '../components/MarcoAutenticacion';
import { colores } from '../theme/colores';
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
  /** Recibe los datos al pulsar «Crear cuenta». Validarlos y enviarlos no es cosa de esta pantalla. */
  onEnviar: (datos: DatosRegistro) => void;
  /** Vuelve a la pantalla anterior. */
  onVolver: () => void;
  /** Lleva al inicio de sesión, para quien ya tiene cuenta. */
  onIniciarSesion: () => void;
}

/** Pantalla de registro (HU-01): correo, contraseña y aceptación de la política de privacidad. */
export function PantallaRegistro({ onEnviar, onVolver, onIniciarSesion }: Props) {
  const [correo, setCorreo] = useState('');
  const [contrasena, setContrasena] = useState('');
  const [privacidadAceptada, setPrivacidadAceptada] = useState(false);

  return (
    <MarcoAutenticacion
      titulo="Crear cuenta"
      subtitulo="Únete para pasear sin sorpresas de calor"
      onVolver={onVolver}
    >
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
        placeholderTextColor={colores.placeholder}
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
        placeholderTextColor={colores.placeholder}
        style={estilos.campo}
      />

      <Pressable
        accessibilityRole="checkbox"
        accessibilityState={{ checked: privacidadAceptada }}
        onPress={() => setPrivacidadAceptada((aceptada) => !aceptada)}
        style={estilosLocal.casilla}
      >
        <View style={[estilosLocal.cuadro, privacidadAceptada && estilosLocal.cuadroMarcado]}>
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
        <Text style={estilosLocal.enlacePoliticaTexto}>Leer la política de privacidad</Text>
      </Pressable>

      <Pressable
        accessibilityRole="button"
        onPress={() => onEnviar({ correo, contrasena, privacidadAceptada })}
        style={estilos.boton}
      >
        <Text style={estilos.textoBoton}>Crear cuenta</Text>
      </Pressable>

      <View style={estilosLocal.contenedorInicioSesion}>
        <Text style={estilosLocal.textoInicioSesion}>¿Ya tienes cuenta?</Text>
        <Pressable
          accessibilityRole="link"
          accessibilityLabel="Inicia sesión"
          onPress={onIniciarSesion}
          hitSlop={8}
        >
          <Text style={estilosLocal.inicioSesionDestacado}>Inicia sesión</Text>
        </Pressable>
      </View>
    </MarcoAutenticacion>
  );
}

/** Solo lo que es exclusivo del registro: la casilla, el enlace a la política y el del login. */
const estilosLocal = StyleSheet.create({
  casilla: {
    alignItems: 'center',
    flexDirection: 'row',
    marginBottom: 8,
    paddingVertical: 4,
  },
  cuadro: {
    alignItems: 'center',
    borderColor: colores.textoSecundario,
    borderRadius: 4,
    borderWidth: 1,
    height: 22,
    justifyContent: 'center',
    marginRight: 10,
    width: 22,
  },
  cuadroMarcado: {
    backgroundColor: colores.verde,
    borderColor: colores.verde,
  },
  marca: {
    color: colores.blanco,
    fontSize: 14,
    fontWeight: 'bold',
  },
  textoCasilla: {
    color: colores.verde,
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
    color: colores.verde,
    fontSize: 13,
    fontWeight: 'bold',
    textDecorationLine: 'underline',
  },
  contenedorInicioSesion: {
    alignItems: 'center',
    columnGap: 4,
    flexDirection: 'row',
    justifyContent: 'center',
    marginTop: 16,
  },
  textoInicioSesion: {
    color: colores.textoSecundario,
    fontSize: 12,
  },
  inicioSesionDestacado: {
    color: colores.verde,
    fontSize: 12,
    fontWeight: 'bold',
  },
});
