import { StyleSheet } from 'react-native';
import { colores } from './colores';

/**
 * Estilos compartidos por las pantallas de autenticación
 * (inicio de sesión y registro).
 */
export const estilosFormulario = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colores.fondo,
    justifyContent: 'center',
    padding: 24,
    overflow: 'hidden',
  },

  volver: {
    position: 'absolute',
    top: 48,
    left: 16,
    padding: 8,
    zIndex: 2,
  },

  enlace: {
    color: colores.verde,
    fontSize: 16,
  },

  formulario: {
    backgroundColor: colores.tarjeta,
    borderRadius: 20,
    padding: 24,
    zIndex: 1,
  },

  logoContainer: {
    alignItems: 'center',
    marginBottom: 24,
  },

  logo: {
    color: colores.verde,
    fontSize: 46,
    fontWeight: '300',
    letterSpacing: 3,
  },

  logoLocation: {
    color: colores.textoSuave,
    fontSize: 11,
    letterSpacing: 5,
    marginTop: 5,
  },

  titulo: {
    color: colores.textoPrincipal,
    fontSize: 26,
    fontWeight: 'bold',
    marginBottom: 8,
  },

  subtitulo: {
    color: colores.textoSecundario,
    fontSize: 15,
    marginBottom: 28,
  },

  etiqueta: {
    color: colores.verde,
    fontSize: 14,
    fontWeight: '600',
    marginBottom: 6,
  },

  campo: {
    backgroundColor: colores.blanco,
    borderColor: colores.borde,
    borderRadius: 10,
    borderWidth: 1,
    color: colores.textoPrincipal,
    fontSize: 16,
    marginBottom: 20,
    paddingHorizontal: 14,
    paddingVertical: 13,
  },

  boton: {
    alignItems: 'center',
    backgroundColor: colores.verde,
    borderRadius: 10,
    marginTop: 8,
    paddingVertical: 15,
  },

  textoBoton: {
    color: colores.blanco,
    fontSize: 16,
    fontWeight: 'bold',
  },

  pie: {
    color: colores.textoSuave,
    fontSize: 10,
    fontWeight: '600',
    letterSpacing: 2,
    textAlign: 'center',
    marginTop: 28,
  },
});
