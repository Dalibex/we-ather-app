import { Text, View } from 'react-native';
import { estilosFormulario as estilos } from '../theme/formulario';

/** Cabecera con el logotipo y la ubicación, común a las pantallas de autenticación. */
export function CabeceraLogo() {
  return (
    <View style={estilos.logoContainer}>
      <Text style={estilos.logo}>WEather</Text>
      <Text style={estilos.logoLocation}>M Á L A G A</Text>
    </View>
  );
}
