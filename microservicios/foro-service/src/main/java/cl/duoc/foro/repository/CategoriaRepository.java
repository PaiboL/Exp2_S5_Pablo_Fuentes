package cl.duoc.foro.repository;

import cl.duoc.foro.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
  boolean existsByNombreIgnoreCase(String nombre);
}
