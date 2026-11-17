import { useState } from 'react';
import { Linking, Pressable, StyleSheet, Text, TextInput, View } from 'react-native';

import { MarcoAutenticacion } from '../components/MarcoAutenticacion';
import { colores } from '../theme/colores';
import { estilosFormulario as estilos } from '../theme/formulario';

/** La política vive en docs/privacidad.md; la app abre la versión publicada en main. */
const URL_POLITICA_PRIVACIDAD =
  'https://github.com/Dalibex/we-ather-app/blob/main/docs/privacidad.md';

// Según el RGPD, la política se informa, no se acepta: el usuario confirma que la ha leído.
const TEXTO_CASILLA = 'Tengo 14 años o más y he leído la política de privacidad';

/** Lo que el usuario ha escrito en el formulario de registro, tal cual, sin validar. */
export interface DatosRegistro {
  correo: string;
  contrasena: string;
  /** Si confirmó tener 14 años o más y haber leído la política (columna `privacidad_aceptada_en`). */
  confirmaEdadYPolitica: boolean;
}

interface Props {
  /** Recibe los datos al pulsar «Crear cuenta». Validarlos y enviarlos no es cosa de esta pantalla. */
  onEnviar: (datos: DatosRegistro) => void;
  /** Vuelve a la pantalla anterior. */
  onVolver: () => void;
  /** Lleva al inicio de sesión, para quien ya tiene cuenta. */
  onIniciarSesion: () => void;
  /** Correo con el que empieza el campo, por ejemplo el que ya se escribió en otra pantalla. */
  correoInicial?: string;
  /** Recibe el correo cada vez que cambia, para conservarlo al pasar a otra pantalla. */
  onCambiarCorreo?: (correo: string) => void;
}

/** Pantalla de registro (HU-01): correo, contraseña, edad mínima y lectura de la política de privacidad. */
export function PantallaRegistro({
  onEnviar,
  onVolver,
  onIniciarSesion,
  correoInicial = '',
  onCambiarCorreo,
}: Props) {
  const [correo, setCorreo] = useState(correoInicial);
  const cambiarCorreo = (nuevo: string) => {
    setCorreo(nuevo);
    onCambiarCorreo?.(nuevo);
  };

  const [contrasena, setContrasena] = useState('');
  const [confirmaEdadYPolitica, setConfirmaEdadYPolitica] = useState(false);

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
        autoComplete="new-password"
        placeholder="Tu contraseña"
        placeholderTextColor={colores.placeholder}
        style={estilos.campo}
      />

      <Pressable
        accessibilityRole="checkbox"
        // Nombre fijo: así el «✓» del cuadro no cambia lo que lee el lector de pantalla.
        accessibilityLabel={TEXTO_CASILLA}
        accessibilityState={{ checked: confirmaEdadYPolitica }}
        onPress={() => setConfirmaEdadYPolitica((marcada) => !marcada)}
        style={estilosLocal.casilla}
      >
        <View style={[estilosLocal.cuadro, confirmaEdadYPolitica && estilosLocal.cuadroMarcado]}>
          {confirmaEdadYPolitica && <Text style={estilosLocal.marca}>✓</Text>}
        </View>
        <Text style={estilosLocal.textoCasilla}>{TEXTO_CASILLA}</Text>
      </Pressable>

      <Pressable
        accessibilityRole="link"
        // Si no se puede abrir (sin navegador o sin conexión), no se hace nada en lugar de fallar.
        onPress={() => void Linking.openURL(URL_POLITICA_PRIVACIDAD).catch(() => {})}
        style={estilosLocal.enlacePolitica}
        hitSlop={14}
      >
        <Text style={estilosLocal.enlacePoliticaTexto}>Leer la política de privacidad</Text>
      </Pressable>

      <Pressable
        accessibilityRole="button"
        onPress={() => onEnviar({ correo, contrasena, confirmaEdadYPolitica })}
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
          hitSlop={14}
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
