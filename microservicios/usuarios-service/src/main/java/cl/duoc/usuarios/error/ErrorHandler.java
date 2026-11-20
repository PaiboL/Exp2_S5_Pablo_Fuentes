package cl.duoc.usuarios.error;

import java.util.*;
import org.slf4j.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ErrorHandler {
  private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

  private ResponseEntity<?> error(int status, String msg, List<Map<String, String>> details) {
    return ResponseEntity.status(status)
        .body(ApiError.builder().status(status).error(msg).details(details).build());
  }

  @ExceptionHandler(ApiException.class)
  ResponseEntity<?> domain(ApiException e) {
    return error(e.status, e.getMessage(), List.of());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<?> validation(MethodArgumentNotValidException e) {
    return error(
        400,
        "Datos invalidos",
        e.getBindingResult().getFieldErrors().stream()
            .map(
                f ->
                    Map.of(
                        "campo",
                        f.getField(),
                        "mensaje",
                        Objects.toString(f.getDefaultMessage(), "Invalido")))
            .toList());
  }

  @ExceptionHandler({
    HttpMessageNotReadableException.class,
    MethodArgumentTypeMismatchException.class
  })
  ResponseEntity<?> malformed(Exception e) {
    return error(400, "JSON o parametro invalido", List.of());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  ResponseEntity<?> conflict(Exception e) {
    log.warn("Conflicto de integridad en persistencia");
    return error(409, "Registro duplicado o con dependencias", List.of());
  }

  @ExceptionHandler(AccessDeniedException.class)
  ResponseEntity<?> denied(Exception e) {
    return error(403, "Acceso denegado", List.of());
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<?> unexpected(Exception e) {
    log.error("Error interno", e);
    return error(500, "Error interno del servicio", List.of());
  }
}
