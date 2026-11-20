package es.uma.weather.usuarios.registro;

/** Cuerpo de {@code POST /api/v1/sesiones}: las credenciales con las que se inicia sesión. */
public record InicioSesionRequest(String correo, String contrasena) {}
