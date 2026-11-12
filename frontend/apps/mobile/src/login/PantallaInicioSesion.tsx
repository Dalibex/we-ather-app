import { useState } from 'react';
import { Pressable, StyleSheet, Text, TextInput, View } from 'react-native';
import { PatronBiznagas } from '../components/PatronBiznagas';
import { CabeceraLogo } from '../components/CabeceraLogo';
import { estilosFormulario as estilos } from '../theme/formulario';

interface DatosInicioSesion {
  correo: string;
  contrasena: string;
}

interface Props {
  onEnviar: (datos: DatosInicioSesion) => void;
  onVolver: () => void;
  onIrRegistro: () => void;
}

/** Pantalla de inicio de sesión. */
export function PantallaInicioSesion({ onEnviar, onVolver, onIrRegistro }: Props) {
  const [correo, setCorreo] = useState('');
  const [contrasena, setContrasena] = useState('');

  return (
    <View style={estilos.container}>
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

        <Text style={estilos.titulo}>Iniciar sesión</Text>
        <Text style={estilos.subtitulo}>
          Tu próximo paseo, un poco más fresco
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
          autoComplete="current-password"
          placeholder="Tu contraseña"
          placeholderTextColor="#94A3A0"
          style={[estilos.campo, estilosLocal.campoContrasena]}
        />

        <View style={estilosLocal.contenedorRegistro}>
          <Text style={estilosLocal.textoRegistro}>¿No tienes cuenta? </Text>
          <Pressable
            accessibilityRole="link"
            accessibilityLabel="Regístrate"
            onPress={onIrRegistro}
            hitSlop={8}
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
      </View>

      <Text style={estilos.pie}>PARA AGUANTAR LA CALOR</Text>
    </View>
  );
}

/** Solo lo que es exclusivo de esta pantalla. */
const estilosLocal = StyleSheet.create({
  campoContrasena: {
    marginBottom: 8,
  },
  contenedorRegistro: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
    alignItems: 'center',
    marginBottom: 24,
  },
  textoRegistro: {
    color: '#718078',
    fontSize: 12,
  },
  registroDestacado: {
    color: '#304B43',
    fontSize: 12,
    fontWeight: 'bold',
  },
});
