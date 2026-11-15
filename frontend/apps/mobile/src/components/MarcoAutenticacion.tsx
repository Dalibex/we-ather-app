import type { ReactNode } from 'react';
import { KeyboardAvoidingView, Platform, Pressable, ScrollView, Text, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

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
 * Marco común de las pantallas de bienvenida, registro e inicio de sesión: fondo de biznagas, botón
 * «Volver», tarjeta con el logotipo, el título y el formulario, y pie.
 *
 * El contenido se desplaza si no cabe (móviles pequeños o texto ampliado) y se aparta del teclado:
 * en iOS lo hace `KeyboardAvoidingView`; en Android, el sistema encoge la ventana. «Volver» queda
 * fuera de la parte que se desplaza y respeta la barra de estado, para que siempre se vea.
 */
export function MarcoAutenticacion({ titulo, subtitulo, onVolver, children }: Props) {
  const margenes = useSafeAreaInsets();

  return (
    <View style={estilos.contenedor}>
      <PatronBiznagas />

      <KeyboardAvoidingView
        testID="marco-teclado"
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}
        style={estilos.evitarTeclado}
      >
        <ScrollView
          testID="marco-desplazable"
          keyboardShouldPersistTaps="handled"
          contentContainerStyle={[
            estilos.contenidoDesplazable,
            { paddingTop: margenes.top + 56, paddingBottom: margenes.bottom + 24 },
          ]}
        >
          <View style={estilos.formulario}>
            <CabeceraLogo />

            <Text style={estilos.titulo}>{titulo}</Text>
            <Text style={estilos.subtitulo}>{subtitulo}</Text>

            {children}
          </View>

          <Text style={estilos.pie}>PARA AGUANTAR LA CALOR</Text>
        </ScrollView>
      </KeyboardAvoidingView>

      {onVolver && (
        <Pressable
          accessibilityRole="button"
          accessibilityLabel="Volver"
          onPress={onVolver}
          style={[estilos.volver, { top: margenes.top + 8 }]}
          hitSlop={8}
        >
          <Text style={estilos.enlace}>← Volver</Text>
        </Pressable>
      )}
    </View>
  );
}
