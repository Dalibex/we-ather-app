import { useEffect, useState } from 'react';
import {
  AccessibilityInfo,
  Linking,
  Pressable,
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';

import { MarcoAutenticacion } from '../components/MarcoAutenticacion';
import { colores } from '../theme/colores';
import { estilosFormulario as estilos } from '../theme/formulario';
import { type ErroresRegistro, validarRegistro } from './validacionRegistro';

/** La política vive en docs/privacidad.md; la app abre la versión publicada en main. */
const URL_POLITICA_PRIVACIDAD =
  'https://github.com/Dalibex/we-ather-app/blob/main/docs/privacidad.md';

// Según el RGPD, la política se informa, no se acepta: el usuario confirma que la ha leído.
const TEXTO_CASILLA = 'Tengo 14 años o más y he leído la política de privacidad';

/** Los datos del formulario de registro. */
export interface DatosRegistro {
  correo: string;
  contrasena: string;
  /** Si confirmó tener 14 años o más y haber leído la política (columna `privacidad_aceptada_en`). */
  confirmaEdadYPolitica: boolean;
}

interface Props {
  /** Recibe los datos ya validados, con el correo sin espacios en los extremos, al pulsar «Crear cuenta». */
  onEnviar: (datos: DatosRegistro) => void;
  /** Mensaje que se muestra si no se ha podido crear la cuenta, por ejemplo porque el correo ya existe. */
  error?: string;
  /** Mientras se espera al servidor, el botón no se puede volver a pulsar. */
  enviando?: boolean;
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
  error,
  enviando = false,
  onVolver,
  onIniciarSesion,
  correoInicial = '',
  onCambiarCorreo,
}: Props) {
  const [errores, setErrores] = useState<ErroresRegistro>({});

  // En Android basta con la región viva del aviso; en iOS, VoiceOver solo lo lee si se anuncia.
  useEffect(() => {
    if (error) {
      AccessibilityInfo.announceForAccessibility(error);
    }
  }, [error]);
  // El error de un campo desaparece en cuanto el usuario lo toca; se vuelve a validar al enviar.
  const borrarError = (campo: keyof ErroresRegistro) =>
    setErrores((anteriores) => ({ ...anteriores, [campo]: undefined }));

  const [correo, setCorreo] = useState(correoInicial);
  const cambiarCorreo = (nuevo: string) => {
    setCorreo(nuevo);
    borrarError('correo');
    onCambiarCorreo?.(nuevo);
  };

  const [contrasena, setContrasena] = useState('');
  const cambiarContrasena = (nueva: string) => {
    setContrasena(nueva);
    borrarError('contrasena');
  };

  const [confirmaEdadYPolitica, setConfirmaEdadYPolitica] = useState(false);
  const cambiarCasilla = () => {
    setConfirmaEdadYPolitica((marcada) => !marcada);
    borrarError('confirmaEdadYPolitica');
  };

  const crearCuenta = () => {
    const datos = { correo, contrasena, confirmaEdadYPolitica };
    const nuevosErrores = validarRegistro(datos);
    setErrores(nuevosErrores);
    if (Object.keys(nuevosErrores).length === 0) {
      onEnviar({ ...datos, correo: correo.trim() });
    }
  };

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
        style={[estilos.campo, errores.correo && estilosLocal.campoConError]}
      />
      <MensajeError mensaje={errores.correo} />

      <Text style={estilos.etiqueta}>Contraseña</Text>
      <TextInput
        accessibilityLabel="Contraseña"
        value={contrasena}
        onChangeText={cambiarContrasena}
        secureTextEntry
        autoCapitalize="none"
        autoComplete="new-password"
        placeholder="Mínimo 8 caracteres"
        placeholderTextColor={colores.placeholder}
        style={[estilos.campo, errores.contrasena && estilosLocal.campoConError]}
      />
      <MensajeError mensaje={errores.contrasena} />

      <Pressable
        accessibilityRole="checkbox"
        // Nombre fijo: así el «✓» del cuadro no cambia lo que lee el lector de pantalla.
        accessibilityLabel={TEXTO_CASILLA}
        accessibilityState={{ checked: confirmaEdadYPolitica }}
        onPress={cambiarCasilla}
        style={estilosLocal.casilla}
      >
        <View style={[estilosLocal.cuadro, confirmaEdadYPolitica && estilosLocal.cuadroMarcado]}>
          {confirmaEdadYPolitica && <Text style={estilosLocal.marca}>✓</Text>}
        </View>
        <Text style={estilosLocal.textoCasilla}>{TEXTO_CASILLA}</Text>
      </Pressable>
      <MensajeError mensaje={errores.confirmaEdadYPolitica} />

      <Pressable
        accessibilityRole="link"
        // Si no se puede abrir (sin navegador o sin conexión), no se hace nada en lugar de fallar.
        onPress={() => void Linking.openURL(URL_POLITICA_PRIVACIDAD).catch(() => {})}
        style={estilosLocal.enlacePolitica}
        hitSlop={14}
      >
        <Text style={estilosLocal.enlacePoliticaTexto}>Leer la política de privacidad</Text>
      </Pressable>

      <MensajeError mensaje={error} />

      <Pressable
        accessibilityRole="button"
        disabled={enviando}
        accessibilityState={{ disabled: enviando, busy: enviando }}
        onPress={crearCuenta}
        style={[estilos.boton, enviando && estilosLocal.botonEnviando]}
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

/** Mensaje de error bajo un campo; el lector de pantalla lo anuncia en cuanto aparece. */
function MensajeError({ mensaje }: { mensaje?: string }) {
  if (!mensaje) return null;
  return (
    <Text accessibilityRole="alert" accessibilityLiveRegion="polite" style={estilosLocal.error}>
      {mensaje}
    </Text>
  );
}

/** Solo lo que es exclusivo del registro: la casilla, el enlace a la política y el del login. */
const estilosLocal = StyleSheet.create({
  botonEnviando: {
    opacity: 0.6,
  },
  campoConError: {
    borderColor: colores.error,
    marginBottom: 6,
  },
  error: {
    color: colores.error,
    fontSize: 13,
    marginBottom: 14,
  },
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
