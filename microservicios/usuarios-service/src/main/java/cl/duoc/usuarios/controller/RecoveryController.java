package cl.duoc.usuarios.controller;

import cl.duoc.usuarios.service.RecoveryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class RecoveryController {
  private final RecoveryService s;

  public RecoveryController(RecoveryService s) {
    this.s = s;
  }

  public record Request(@NotBlank @Email @Size(max = 150) String email) {}

  public record Reset(
      @NotBlank @Size(max = 128) String token,
      @NotBlank @Size(min = 10, max = 64) String password) {}

  @PostMapping("/recuperar")
  public Object request(@Valid @RequestBody Request r) {
    s.request(r.email());
    return Map.of("mensaje", "Si la cuenta existe, recibirás instrucciones en su correo.");
  }

  @PostMapping("/restablecer")
  public Object reset(@Valid @RequestBody Reset r) {
    s.reset(r.token(), r.password());
    return Map.of("mensaje", "Contraseña actualizada. Inicia sesión nuevamente.");
  }
}
