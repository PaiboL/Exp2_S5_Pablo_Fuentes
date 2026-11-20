package cl.duoc.usuarios.controller;

import cl.duoc.usuarios.dto.UserDtos.*;
import cl.duoc.usuarios.service.UserService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {
  private final UserService service;

  public UserController(UserService s) {
    service = s;
  }

  @PostMapping("/auth/registro")
  public ResponseEntity<Vista> register(@Valid @RequestBody Registro r) {
    var u = service.register(r);
    return ResponseEntity.created(URI.create("/api/usuarios/" + u.id())).body(u);
  }

  @PostMapping("/auth/login")
  public Token login(@Valid @RequestBody Login r) {
    return service.login(r);
  }

  @GetMapping("/usuarios/me")
  public Vista me(@AuthenticationPrincipal Jwt jwt) {
    return service.get(Long.valueOf(jwt.getSubject()));
  }

  @GetMapping("/usuarios")
  @PreAuthorize("hasRole('ADMIN')")
  public Object all(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return service.all(page, size);
  }

  @GetMapping("/roles")
  @PreAuthorize("hasRole('ADMIN')")
  public Object roles() {
    return service.roles();
  }

  @GetMapping("/usuarios/{id}")
  @PreAuthorize("hasRole('ADMIN') or #id.toString() == authentication.name")
  public Vista get(@PathVariable Long id) {
    return service.get(id);
  }

  @PutMapping("/usuarios/{id}")
  @PreAuthorize("hasRole('ADMIN') or #id.toString() == authentication.name")
  public Vista update(@PathVariable Long id, @Valid @RequestBody Actualizar r) {
    return service.update(id, r);
  }

  @PutMapping("/usuarios/{id}/rol")
  @PreAuthorize("hasRole('ADMIN')")
  public Vista role(
      @PathVariable Long id, @Valid @RequestBody CambiarRol r, @AuthenticationPrincipal Jwt jwt) {
    return service.role(id, r, Long.valueOf(jwt.getSubject()));
  }

  @DeleteMapping("/usuarios/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
    service.delete(id, Long.valueOf(jwt.getSubject()));
    return ResponseEntity.noContent().build();
  }
}
