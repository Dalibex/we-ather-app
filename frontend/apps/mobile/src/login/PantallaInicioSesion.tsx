import { useState } from 'react';
import { Pressable, StyleSheet, Text, TextInput, View } from 'react-native';

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

  return (
    <MarcoAutenticacion
      titulo="Iniciar sesión"
      subtitulo="Tu próximo paseo, un poco más fresco"
      onVolver={onVolver}
    >
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

      <Pressable
        accessibilityRole="button"
        onPress={() => onEnviar({ correo, contrasena })}
        style={estilos.boton}
      >
        <Text style={estilos.textoBoton}>Iniciar sesión</Text>
      </Pressable>
    </MarcoAutenticacion>
  );
}

/** Solo lo que es exclusivo del inicio de sesión: el enlace al registro. */
const estilosLocal = StyleSheet.create({
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
