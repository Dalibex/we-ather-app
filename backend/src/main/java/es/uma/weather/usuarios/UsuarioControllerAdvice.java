package es.uma.weather.usuarios;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduce los errores del registro y del inicio de sesión a respuestas JSON {@code {"codigo",
 * "mensaje"}}.
 */
@RestControllerAdvice(assignableTypes = {UsuarioController.class, SesionController.class})
class UsuarioControllerAdvice {

    record ErrorResponse(String codigo, String mensaje) {}

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ErrorResponse> datosInvalidos(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("DATOS_INVALIDOS", e.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ErrorResponse> cuerpoIlegible(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        new ErrorResponse(
                                "DATOS_INVALIDOS",
                                "El cuerpo de la petición no es un JSON válido"));
    }

    // Un único mensaje para cualquier fallo de credenciales: Spring Security también da
    // BadCredentialsException si el correo no existe, así que no se revela qué correos están
    // registrados. Los fallos internos (la base de datos caída) no entran aquí: son un 500.
    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<ErrorResponse> credencialesInvalidas(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                        new ErrorResponse(
                                "CREDENCIALES_INVALIDAS", "Correo o contraseña incorrectos"));
    }

    @ExceptionHandler(CorreoYaRegistradoException.class)
    ResponseEntity<ErrorResponse> correoYaRegistrado(CorreoYaRegistradoException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("CORREO_YA_REGISTRADO", e.getMessage()));
    }
}
