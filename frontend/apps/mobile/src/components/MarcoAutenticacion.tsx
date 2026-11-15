import { useEffect, useState, type ReactNode } from 'react';
import { Animated, Keyboard, Pressable, Text, View } from 'react-native';

import { estilosFormulario as estilos } from '../theme/formulario';
import { CabeceraLogo } from './CabeceraLogo';
import { PatronBiznagas } from './PatronBiznagas';

interface Props {
  titulo: string;
  subtitulo: string;
  /** Si no se indica, no hay botón «Volver» (por ejemplo, en la primera pantalla). */
  onVolver?: () => void;
  /** Campos y botones del formulario. */
  children: ReactNode;
}

/**
 * Marco común de las pantallas de bienvenida, registro e inicio de sesión: fondo de biznagas, botón «Volver»,
 * tarjeta con el logotipo, el título y el formulario, y pie. Cuando aparece el teclado, sube el
 * contenido para que no tape los campos de abajo.
 */
export function MarcoAutenticacion({ titulo, subtitulo, onVolver, children }: Props) {
  const [tecladoVisible, setTecladoVisible] = useState(false);
  const [desplazamiento] = useState(() => new Animated.Value(0));

  useEffect(() => {
    const mostrar = Keyboard.addListener('keyboardDidShow', () => setTecladoVisible(true));
    const ocultar = Keyboard.addListener('keyboardDidHide', () => setTecladoVisible(false));

    return () => {
      mostrar.remove();
      ocultar.remove();
    };
  }, []);

  useEffect(() => {
    const animacion = Animated.timing(desplazamiento, {
      toValue: tecladoVisible ? 1 : 0,
      duration: 500,
      useNativeDriver: false,
    });

    animacion.start();

    return () => animacion.stop();
  }, [tecladoVisible, desplazamiento]);

  const paddingTop = desplazamiento.interpolate({ inputRange: [0, 1], outputRange: [0, 8] });
  const posicion = desplazamiento.interpolate({ inputRange: [0, 1], outputRange: [0, -100] });

  return (
    <Animated.View
      style={[estilos.container, { paddingTop, transform: [{ translateY: posicion }] }]}
    >
      <PatronBiznagas />

      {onVolver && (
        <Pressable
          accessibilityRole="button"
          accessibilityLabel="Volver"
          onPress={onVolver}
          style={estilos.volver}
        >
          <Text style={estilos.enlace}>← Volver</Text>
        </Pressable>
      )}

      <View style={estilos.formulario}>
        <CabeceraLogo />

        <Text style={estilos.titulo}>{titulo}</Text>
        <Text style={estilos.subtitulo}>{subtitulo}</Text>

        {children}
      </View>

      <Text style={estilos.pie}>PARA AGUANTAR LA CALOR</Text>
    </Animated.View>
  );
}
