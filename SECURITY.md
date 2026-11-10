# Política de seguridad

## Versiones con soporte

WEather todavía no tiene ninguna versión publicada. Las correcciones de seguridad se aplican sobre
el último código de la rama principal.

## Informar de una vulnerabilidad

**No abras una issue, pull request o discusión pública para una vulnerabilidad.** Un aviso público
expone el problema antes de que esté corregido.

Infórmala de forma privada:

1. Abre la pestaña **Security** del repositorio y elige **Report a vulnerability** (aviso privado
   de vulnerabilidades de GitHub).
2. Describe el problema, la parte afectada, los pasos para reproducirlo y su posible impacto.
3. No incluyas datos personales reales, contraseñas ni tokens.

El equipo confirma el aviso, acuerda una corrección y la publica cuando está resuelta.

## Lo que nunca se versiona

- Archivos `.env`, contraseñas, claves de API, tokens o cualquier otro secreto. Solo se versiona
  `.env.example`, con valores de ejemplo.
- Datos personales reales: nombres, correos, ubicaciones o fotos de personas, tampoco en pruebas,
  capturas, issues o pull requests.
- Volcados de base de datos.

Si se sube un secreto por error, avisa al equipo enseguida y revócalo o cámbialo. Borrarlo del
último commit no basta: sigue en el historial de Git.
