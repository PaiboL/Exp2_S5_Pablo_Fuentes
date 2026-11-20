package cl.duoc.moderacion.controller;

import cl.duoc.moderacion.service.ModerationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/moderaciones")
@PreAuthorize("hasRole('ADMIN')")
public class ModerationController {
  private final ModerationService s;

  public ModerationController(ModerationService s) {
    this.s = s;
  }

  public record Crear(
      @NotBlank @Pattern(regexp = "TEMA|COMENTARIO") String tipo,
      @NotNull @Positive Long recursoId,
      @NotBlank @Size(max = 300) String motivo) {}

  public record Editar(@NotBlank @Size(max = 300) String motivo, @NotNull Boolean bloqueado) {}

  @GetMapping
  public Object all() {
    return s.all();
  }

  @GetMapping("/{id}")
  public Object get(@PathVariable Long id) {
    return s.get(id);
  }

  @PostMapping
  public ResponseEntity<?> create(
      @Valid @RequestBody Crear r, Authentication a, @RequestHeader("Authorization") String token) {
    var d = s.create(r.tipo(), r.recursoId(), r.motivo(), Long.valueOf(a.getName()), token);
    return ResponseEntity.created(URI.create("/api/moderaciones/" + d.id)).body(d);
  }

  @PutMapping("/{id}")
  public Object update(
      @PathVariable Long id,
      @Valid @RequestBody Editar r,
      Authentication a,
      @RequestHeader("Authorization") String token) {
    return s.update(id, r.motivo(), r.bloqueado(), Long.valueOf(a.getName()), token);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @PathVariable Long id, @RequestHeader("Authorization") String token) {
    s.delete(id, token);
    return ResponseEntity.noContent().build();
  }
}
