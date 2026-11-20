import { useEffect, useState } from 'react';
import { AccessibilityInfo, Pressable, StyleSheet, Text, TextInput, View } from 'react-native';

import { MarcoAutenticacion } from '../components/MarcoAutenticacion';
import { colores } from '../theme/colores';
import { estilosFormulario as estilos } from '../theme/formulario';

/** Lo que el usuario ha escrito en el formulario de inicio de sesión, tal cual, sin validar. */
export interface DatosInicioSesion {
  correo: string;
  contrasena: string;
}

interface Props {
  /** Recibe los datos al pulsar «Iniciar sesión». Enviarlos al servidor no es cosa de esta pantalla. */
  onEnviar: (datos: DatosInicioSesion) => void;
  /** Mensaje que se muestra si no se ha podido iniciar sesión, por ejemplo con credenciales incorrectas. */
  error?: string;
  /** Mientras se espera al servidor, el botón no se puede volver a pulsar. */
  enviando?: boolean;
  /** Aviso informativo encima del formulario, por ejemplo que la cuenta se acaba de crear. */
  aviso?: string;
  /** Vuelve a la pantalla anterior. */
  onVolver: () => void;
  /** Lleva al registro, para quien todavía no tiene cuenta. */
  onIrRegistro: () => void;
  /** Correo con el que empieza el campo, por ejemplo el que ya se escribió en otra pantalla. */
  correoInicial?: string;
  /** Recibe el correo cada vez que cambia, para conservarlo al pasar a otra pantalla. */
  onCambiarCorreo?: (correo: string) => void;
}

/** Pantalla de inicio de sesión (HU-02): correo y contraseña. */
export function PantallaInicioSesion({
  onEnviar,
  error,
  enviando = false,
  aviso,
  onVolver,
  onIrRegistro,
  correoInicial = '',
  onCambiarCorreo,
}: Props) {
  const [correo, setCorreo] = useState(correoInicial);
  const cambiarCorreo = (nuevo: string) => {
    setCorreo(nuevo);
    onCambiarCorreo?.(nuevo);
  };

  const [contrasena, setContrasena] = useState('');

  // En Android basta con la región viva del aviso; en iOS, VoiceOver solo lo lee si se anuncia.
  useEffect(() => {
    if (error) {
      AccessibilityInfo.announceForAccessibility(error);
    }
  }, [error]);
  useEffect(() => {
    if (aviso) {
      AccessibilityInfo.announceForAccessibility(aviso);
    }
  }, [aviso]);

  return (
    <MarcoAutenticacion
      titulo="Iniciar sesión"
      subtitulo="Tu próximo paseo, un poco más fresco"
      onVolver={onVolver}
    >
      {aviso ? (
        <Text accessibilityLiveRegion="polite" style={estilosLocal.aviso}>
          {aviso}
        </Text>
      ) : null}

      <Text style={estilos.etiqueta}>Correo electrónico</Text>
      <TextInput
        accessibilityLabel="Correo electrónico"
        value={correo}
        onChangeText={cambiarCorreo}
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
        autoComplete="current-password"
        placeholder="Tu contraseña"
        placeholderTextColor={colores.placeholder}
        style={[estilos.campo, estilosLocal.campoContrasena]}
      />

      <View style={estilosLocal.contenedorRegistro}>
        <Text style={estilosLocal.textoRegistro}>¿No tienes cuenta?</Text>
        <Pressable
          accessibilityRole="link"
          accessibilityLabel="Regístrate"
          onPress={onIrRegistro}
          hitSlop={14}
        >
          <Text style={estilosLocal.registroDestacado}>Regístrate</Text>
        </Pressable>
      </View>

      {error ? (
        <Text accessibilityRole="alert" accessibilityLiveRegion="polite" style={estilosLocal.error}>
          {error}
        </Text>
      ) : null}

      <Pressable
        accessibilityRole="button"
        disabled={enviando}
        accessibilityState={{ disabled: enviando, busy: enviando }}
        // No se devuelve la promesa de onEnviar: la pantalla no espera al servidor, solo lo indica
        // con «enviando».
        onPress={() => {
          onEnviar({ correo, contrasena });
        }}
        style={[estilos.boton, enviando && estilosLocal.botonEnviando]}
      >
        <Text style={estilos.textoBoton}>Iniciar sesión</Text>
      </Pressable>
    </MarcoAutenticacion>
  );
}

/** Solo lo que es exclusivo del inicio de sesión: el enlace al registro y el aviso de error. */
const estilosLocal = StyleSheet.create({
  error: {
    color: colores.error,
    fontSize: 13,
    marginBottom: 14,
    textAlign: 'center',
  },
  aviso: {
    color: colores.verde,
    fontSize: 13,
    fontWeight: '600',
    marginBottom: 14,
    textAlign: 'center',
  },
  botonEnviando: {
    opacity: 0.6,
  },
  campoContrasena: {
    marginBottom: 8,
  },
  contenedorRegistro: {
    alignItems: 'center',
    columnGap: 4,
    flexDirection: 'row',
    justifyContent: 'flex-end',
    marginBottom: 24,
  },
  textoRegistro: {
    color: colores.textoSecundario,
    fontSize: 12,
  },
  registroDestacado: {
    color: colores.verde,
    fontSize: 12,
    fontWeight: 'bold',
  },
});
