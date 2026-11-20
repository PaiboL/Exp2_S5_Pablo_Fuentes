package cl.duoc.foro.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class ForoDtos {
  public record CategoriaInput(
      @NotBlank @Size(max = 80) String nombre, @NotBlank @Size(max = 250) String descripcion) {}

  public record TemaInput(
      @NotBlank @Size(max = 150) String titulo,
      @NotBlank @Size(max = 4000) String contenido,
      @NotNull @Positive Long categoriaId) {}

  public record ComentarioInput(@NotBlank @Size(max = 2000) String contenido) {}

  public record CategoriaVista(Long id, String nombre, String descripcion) {}

  public record TemaVista(
      Long id,
      String titulo,
      String contenido,
      Long autorId,
      Long categoriaId,
      String categoria,
      LocalDateTime creadoEn) {}

  public record ComentarioVista(
      Long id, String contenido, Long autorId, Long temaId, LocalDateTime creadoEn) {}
}
