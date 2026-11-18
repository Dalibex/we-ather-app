package es.uma.weather.usuarios.registro;

import java.util.UUID;

public record RegistroResponse(UUID id, String correo, String mensaje) {}
