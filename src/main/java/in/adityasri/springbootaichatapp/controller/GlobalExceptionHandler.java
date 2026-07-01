package in.adityasri.springbootaichatapp.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.Instant;
import java.util.Map;

/**
 * — @RestControllerAdvice:
 *   A central place to translate exceptions into clean JSON responses so
 *   clients never see raw stack traces, and every error has the same shape.
 *
 *   Two validation exceptions are handled:
 *     - MethodArgumentNotValidException -> @Valid on a @RequestBody failed
 *     - ConstraintViolationException    -> @NotBlank/@Size on a @RequestParam failed
 *   Anything else falls through to the catch-all as a 502 (the upstream AI call failed).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleBodyValidation(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(e -> e.getField() + " " + e.getDefaultMessage())
                .orElse("Invalid request");
        return build(HttpStatus.BAD_REQUEST, detail);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleParamValidation(ConstraintViolationException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Spring MVC's built-in method validation (@NotBlank/@Size on @RequestParam)
    // raises this; handle it explicitly so it isn't swallowed by the 502 catch-all.
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, Object>> handleParamValidation(HandlerMethodValidationException ex) {
        return build(HttpStatus.BAD_REQUEST, "Invalid request parameters");
    }

    // A required @RequestParam was omitted entirely — a client error, not a server one.
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParam(MissingServletRequestParameterException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
        // The AI provider (bad key, rate limit, network, invalid model) surfaces here.
        return build(HttpStatus.BAD_GATEWAY, "Chat request failed: " + ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message
        ));
    }
}
