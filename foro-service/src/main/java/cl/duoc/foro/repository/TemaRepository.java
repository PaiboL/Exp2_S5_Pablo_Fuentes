package cl.duoc.foro.repository;

import cl.duoc.foro.model.Tema;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemaRepository
    extends JpaRepository<Tema, Long>,
        org.springframework.data.jpa.repository.JpaSpecificationExecutor<Tema> {
  boolean existsByCategoriaId(Long categoriaId);
}
