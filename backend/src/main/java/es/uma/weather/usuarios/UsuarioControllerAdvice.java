package es.uma.weather.usuarios;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Traduce los errores del registro a respuestas JSON {@code {"codigo", "mensaje"}}. */
@RestControllerAdvice(assignableTypes = UsuarioController.class)
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

    @ExceptionHandler(CorreoYaRegistradoException.class)
    ResponseEntity<ErrorResponse> correoYaRegistrado(CorreoYaRegistradoException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("CORREO_YA_REGISTRADO", e.getMessage()));
    }
}
