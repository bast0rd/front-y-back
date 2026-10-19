package com.andesstay.admin_rabbit.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.Instant;
import java.util.List;

/** Respuestas de error claras y consistentes para toda la API de administración. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ApiError(String timestamp, int status, String error, List<String> details) {
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleBodyValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getAllErrors().stream()
                .map(e -> (e instanceof FieldError fe ? fe.getField() + ": " : "") + e.getDefaultMessage())
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Datos de entrada inválidos", details);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiError> handleParamValidation(HandlerMethodValidationException ex) {
        List<String> details = ex.getAllErrors().stream().map(MessageSourceResolvable::getDefaultMessage).toList();
        return build(HttpStatus.BAD_REQUEST, "Parámetros inválidos", details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraint(ConstraintViolationException ex) {
        List<String> details = ex.getConstraintViolations().stream().map(v -> v.getMessage()).toList();
        return build(HttpStatus.BAD_REQUEST, "Parámetros inválidos", details);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, "Solicitud inválida", List.of(ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "El cuerpo de la petición no es un JSON válido", List.of());
    }

    /** RabbitMQ respondió con error (404 cola inexistente, 400 argumentos distintos a los de la cola existente...). */
    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ApiError> handleRabbitError(RestClientResponseException ex) {
        HttpStatusCode status = ex.getStatusCode();
        String reason = status.value() == 404
                ? "El recurso no existe en RabbitMQ"
                : "RabbitMQ rechazó la operación";
        return ResponseEntity.status(status).body(new ApiError(Instant.now().toString(), status.value(), reason,
                List.of(ex.getResponseBodyAsString())));
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ApiError> handleUnreachable(ResourceAccessException ex) {
        return build(HttpStatus.BAD_GATEWAY, "No se pudo conectar con la Management API de RabbitMQ",
                List.of(ex.getMessage()));
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String error, List<String> details) {
        return ResponseEntity.status(status)
                .body(new ApiError(Instant.now().toString(), status.value(), error, details));
    }
}
