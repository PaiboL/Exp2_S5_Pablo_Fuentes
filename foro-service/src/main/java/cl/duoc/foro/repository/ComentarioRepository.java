package cl.duoc.foro.repository;

import cl.duoc.foro.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
  org.springframework.data.domain.Page<Comentario> findByTemaId(
      Long temaId, org.springframework.data.domain.Pageable pageable);

  void deleteByTemaId(Long temaId);
}
