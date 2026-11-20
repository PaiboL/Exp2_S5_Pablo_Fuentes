package cl.duoc.foro.controller;

import cl.duoc.foro.dto.ForoDtos.*;
import cl.duoc.foro.service.ForoService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ForoController {
  private final ForoService s;

  public ForoController(ForoService s) {
    this.s = s;
  }

  @GetMapping("/categorias")
  public Object categories() {
    return s.categories();
  }

  @GetMapping("/categorias/{id}")
  public Object category(@PathVariable Long id) {
    return s.category(id);
  }

  @PostMapping("/categorias")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> createCategory(@Valid @RequestBody CategoriaInput r) {
    var c = s.saveCategory(null, r);
    return ResponseEntity.created(URI.create("/api/categorias/" + c.id())).body(c);
  }

  @PutMapping("/categorias/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public Object updateCategory(@PathVariable Long id, @Valid @RequestBody CategoriaInput r) {
    return s.saveCategory(id, r);
  }

  @DeleteMapping("/categorias/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
    s.deleteCategory(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/temas")
  public Object search(
      @RequestParam(defaultValue = "") String q,
      @RequestParam(required = false) Long categoriaId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return s.search(q, categoriaId, page, size);
  }

  @GetMapping("/temas/{id}")
  public Object topic(@PathVariable Long id) {
    return s.getTopic(id);
  }

  @PostMapping("/temas")
  public ResponseEntity<?> publish(@Valid @RequestBody TemaInput r, Authentication a) {
    var t = s.saveTopic(null, r, a);
    return ResponseEntity.created(URI.create("/api/temas/" + t.id())).body(t);
  }

  @PutMapping("/temas/{id}")
  public Object updateTopic(
      @PathVariable Long id, @Valid @RequestBody TemaInput r, Authentication a) {
    return s.saveTopic(id, r, a);
  }

  @DeleteMapping("/temas/{id}")
  public ResponseEntity<Void> deleteTopic(@PathVariable Long id, Authentication a) {
    s.deleteTopic(id, a);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/temas/{id}/comentarios")
  public Object comments(
      @PathVariable Long id,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return s.comments(id, page, size);
  }

  @PostMapping("/temas/{id}/comentarios")
  public ResponseEntity<?> comment(
      @PathVariable Long id, @Valid @RequestBody ComentarioInput r, Authentication a) {
    var c = s.addComment(id, r, a);
    return ResponseEntity.created(URI.create("/api/comentarios/" + c.id())).body(c);
  }

  @GetMapping("/comentarios/{id}")
  public Object comment(@PathVariable Long id) {
    return s.getComment(id);
  }

  @PutMapping("/comentarios/{id}")
  public Object updateComment(
      @PathVariable Long id, @Valid @RequestBody ComentarioInput r, Authentication a) {
    return s.updateComment(id, r, a);
  }

  @DeleteMapping("/comentarios/{id}")
  public ResponseEntity<Void> deleteComment(@PathVariable Long id, Authentication a) {
    s.deleteComment(id, a);
    return ResponseEntity.noContent().build();
  }
}
