import { useState } from 'react';
import { Linking, Pressable, StyleSheet, Text, TextInput, View } from 'react-native';

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

  onIniciarSesion: () => void;
}

/** Pantalla de registro (HU-01): correo, contraseña y aceptación de la política de privacidad. */
export function PantallaRegistro({ onEnviar, onVolver}: Props) {
  const [correo, setCorreo] = useState('');
  const [contrasena, setContrasena] = useState('');
  const [privacidadAceptada, setPrivacidadAceptada] = useState(false);

  return (
    <View style={styles.container}>
      <Pressable accessibilityRole="button" onPress={onVolver} style={styles.volver}>
        <Text style={styles.enlace}>Volver</Text>
      </Pressable>

      <Text style={styles.titulo}>Crear cuenta</Text>

      <Text style={styles.etiqueta}>Correo electrónico</Text>
      <TextInput
        accessibilityLabel="Correo electrónico"
        value={correo}
        onChangeText={setCorreo}
        keyboardType="email-address"
        autoCapitalize="none"
        autoComplete="email"
        autoCorrect={false}
        style={styles.campo}
      />

      <Text style={styles.etiqueta}>Contraseña</Text>
      <TextInput
        accessibilityLabel="Contraseña"
        value={contrasena}
        onChangeText={setContrasena}
        secureTextEntry
        autoCapitalize="none"
        autoComplete="new-password"
        style={styles.campo}
      />

      <Pressable
        accessibilityRole="checkbox"
        accessibilityState={{ checked: privacidadAceptada }}
        onPress={() => setPrivacidadAceptada((aceptada) => !aceptada)}
        style={styles.casilla}
      >
        <View style={[styles.cuadro, privacidadAceptada && styles.cuadroMarcado]}>
          {privacidadAceptada && <Text style={styles.marca}>✓</Text>}
        </View>
        <Text style={styles.textoCasilla}>
          Tengo 14 años o más y acepto la política de privacidad
        </Text>
      </Pressable>

      <Pressable
        accessibilityRole="link"
        onPress={() => Linking.openURL(URL_POLITICA_PRIVACIDAD)}
        style={styles.enlacePolitica}
      >
        <Text style={styles.enlace}>Leer la política de privacidad</Text>
      </Pressable>

      <Pressable
        accessibilityRole="button"
        onPress={() => onEnviar({ correo, contrasena, privacidadAceptada })}
        style={styles.boton}
      >
        <Text style={styles.textoBoton}>Crear cuenta</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#FFFFFF',
    justifyContent: 'center',
    padding: 16,
  },
  volver: {
    position: 'absolute',
    top: 48,
    left: 16,
    padding: 8,
  },
  enlace: {
    color: '#2563EB',
    fontSize: 16,
  },
  titulo: {
    color: '#0F172A',
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 24,
  },
  etiqueta: {
    color: '#0F172A',
    fontSize: 14,
    marginBottom: 4,
  },
  campo: {
    borderColor: '#CBD5E1',
    borderRadius: 8,
    borderWidth: 1,
    color: '#0F172A',
    fontSize: 16,
    marginBottom: 16,
    paddingHorizontal: 12,
    paddingVertical: 10,
  },
  casilla: {
    alignItems: 'center',
    flexDirection: 'row',
    marginBottom: 8,
    paddingVertical: 4,
  },
  enlacePolitica: {
    alignSelf: 'flex-start',
    marginBottom: 24,
    paddingVertical: 4,
  },
  cuadro: {
    alignItems: 'center',
    borderColor: '#64748B',
    borderRadius: 4,
    borderWidth: 1,
    height: 24,
    justifyContent: 'center',
    marginRight: 8,
    width: 24,
  },
  cuadroMarcado: {
    backgroundColor: '#2563EB',
    borderColor: '#2563EB',
  },
  marca: {
    color: '#FFFFFF',
    fontSize: 16,
  },
  textoCasilla: {
    color: '#0F172A',
    flexShrink: 1,
    fontSize: 14,
  },
  boton: {
    alignItems: 'center',
    backgroundColor: '#2563EB',
    borderRadius: 8,
    paddingVertical: 12,
  },
  textoBoton: {
    color: '#FFFFFF',
    fontSize: 16,
    fontWeight: 'bold',
  },
});
