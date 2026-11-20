package cl.duoc.foro.controller;

import cl.duoc.foro.service.ForoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contenido-admin")
@PreAuthorize("hasRole('ADMIN')")
public class ContentModerationController {
  private final ForoService s;

  public ContentModerationController(ForoService s) {
    this.s = s;
  }

  public record Estado(@NotNull Boolean bloqueado) {}

  @GetMapping("/temas")
  public Object topics() {
    return s.inspectTopics();
  }

  @GetMapping("/comentarios")
  public Object comments() {
    return s.inspectComments();
  }

  @PutMapping("/{tipo}/{id}")
  public Object apply(
      @PathVariable String tipo, @PathVariable Long id, @Valid @RequestBody Estado e) {
    s.moderate(tipo, id, e.bloqueado());
    return Map.of("bloqueado", e.bloqueado());
  }
}
