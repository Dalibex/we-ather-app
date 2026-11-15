import { Text, View } from 'react-native';
import { estilosFormulario as estilos } from '../theme/formulario';

/** Cabecera con el logotipo y la ubicación, común a las pantallas de bienvenida, registro e inicio de sesión. */
export function CabeceraLogo() {
  return (
    <View style={estilos.contenedorLogo}>
      <Text style={estilos.logo}>WEather</Text>
      <Text style={estilos.ubicacionLogo}>M Á L A G A</Text>
    </View>
  );
}
