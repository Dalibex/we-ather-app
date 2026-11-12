
import { useState } from 'react';
import {
  Image,
  Pressable,
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';
import iconoBiznaga from '../../assets/icono-biznaga-malaga.png';

interface DatosInicioSesion {
  correo: string;
  contrasena: string;
}

interface Props {
  onEnviar: (datos: DatosInicioSesion) => void;
  onVolver: () => void;
  onIrRegistro: () => void;
}

/** Patrón decorativo de biznagas para el fondo. */
function PatronBiznagas() {
  return (
    <View pointerEvents="none" style={styles.patron}>
      {Array.from({ length: 18 }, (_, index) => (
        <View key={index} style={styles.baldosa}>
          <Image
            source={iconoBiznaga}
            style={[
              styles.iconoBiznaga,
              {
                transform: [
                  { rotate: `${(index % 4) * 20}deg` },
                ],
              },
            ]}
            resizeMode="contain"
          />
        </View>
      ))}
    </View>
  );
}

/** Pantalla de inicio de sesión. */
export function PantallaInicioSesion({
  onEnviar,
  onVolver,
  onIrRegistro,
}: Props) {
  const [correo, setCorreo] = useState('');
  const [contrasena, setContrasena] = useState('');

  return (
    <View style={styles.container}>
      <PatronBiznagas />

      <Pressable
        accessibilityRole="button"
        accessibilityLabel="Volver"
        onPress={onVolver}
        style={styles.volver}
      >
        <Text style={styles.enlace}>← Volver</Text>
      </Pressable>

      <View style={styles.formulario}>
        <View style={styles.logoContainer}>
          <Text style={styles.logo}>WEather</Text>
          <Text style={styles.logoLocation}>M Á L A G A</Text>
        </View>

        <Text style={styles.titulo}>Iniciar sesión</Text>

        <Text style={styles.subtitulo}>
          Tu próximo paseo, un poco más fresco
        </Text>

        <Text style={styles.etiqueta}>
          Correo electrónico
        </Text>

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
          style={styles.campo}
        />

        <Text style={styles.etiqueta}>
          Contraseña
        </Text>

        <TextInput
          accessibilityLabel="Contraseña"
          value={contrasena}
          onChangeText={setContrasena}
          secureTextEntry
          autoCapitalize="none"
          autoComplete="current-password"
          placeholder="Tu contraseña"
          placeholderTextColor="#94A3A0"
          style={[styles.campo, styles.campoContrasena]}
        />

        <View style={styles.contenedorRegistro}>
          <Text style={styles.textoRegistro}>
            ¿No tienes cuenta?{' '}
          </Text>

          <Pressable
            accessibilityRole="link"
            accessibilityLabel="Regístrate"
            onPress={onIrRegistro}
            hitSlop={8}
          >
            <Text style={styles.registroDestacado}>
              Regístrate
            </Text>
          </Pressable>
        </View>

        <Pressable
          accessibilityRole="button"
          onPress={() => onEnviar({ correo, contrasena })}
          style={styles.boton}
        >
          <Text style={styles.textoBoton}>
            Iniciar sesión
          </Text>
        </Pressable>
      </View>

      <Text style={styles.pie}>
        PARA AGUANTAR LA CALOR
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F5F2EA',
    justifyContent: 'center',
    padding: 24,
    overflow: 'hidden',
  },

  patron: {
    ...StyleSheet.absoluteFill,
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-around',
    alignContent: 'space-around',
  },

  baldosa: {
    width: '33.33%',
    height: '20%',
    justifyContent: 'center',
    alignItems: 'center',
  },

  iconoBiznaga: {
    width: 42,
    height: 42,
    opacity: 0.10,
  },

  volver: {
    position: 'absolute',
    top: 48,
    left: 16,
    padding: 8,
    zIndex: 2,
  },

  enlace: {
    color: '#304B43',
    fontSize: 16,
  },

  formulario: {
    backgroundColor: 'rgba(255, 255, 255, 0.88)',
    borderRadius: 20,
    padding: 24,
    zIndex: 1,
  },

  titulo: {
    color: '#263D38',
    fontSize: 26,
    fontWeight: 'bold',
    marginBottom: 8,
  },

  subtitulo: {
    color: '#718078',
    fontSize: 15,
    marginBottom: 28,
  },

  etiqueta: {
    color: '#304B43',
    fontSize: 14,
    fontWeight: '600',
    marginBottom: 6,
  },

  campo: {
    backgroundColor: '#FFFFFF',
    borderColor: '#D7D9D1',
    borderRadius: 10,
    borderWidth: 1,
    color: '#263D38',
    fontSize: 16,
    marginBottom: 20,
    paddingHorizontal: 14,
    paddingVertical: 13,
  },

  campoContrasena: {
    marginBottom: 8,
  },

  contenedorRegistro: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
    alignItems: 'center',
    marginBottom: 12,
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

  boton: {
    alignItems: 'center',
    backgroundColor: '#304B43',
    borderRadius: 10,
    marginTop: 8,
    paddingVertical: 15,
  },

  textoBoton: {
    color: '#FFFFFF',
    fontSize: 16,
    fontWeight: 'bold',
  },

  pie: {
    color: '#849087',
    fontSize: 10,
    fontWeight: '600',
    letterSpacing: 2,
    textAlign: 'center',
    marginTop: 28,
  },

  logoContainer: {
    alignItems: 'center',
    marginBottom: 24,
  },

  logo: {
    color: '#304B43',
    fontSize: 46,
    fontWeight: '300',
    letterSpacing: 3,
  },

  logoLocation: {
    color: '#849087',
    fontSize: 11,
    letterSpacing: 5,
    marginTop: 5,
  },
});
