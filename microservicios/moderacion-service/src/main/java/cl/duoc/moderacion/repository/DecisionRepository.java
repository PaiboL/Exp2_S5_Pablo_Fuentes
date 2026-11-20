package cl.duoc.moderacion.repository;

import cl.duoc.moderacion.model.Decision;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DecisionRepository extends JpaRepository<Decision, Long> {
  Optional<Decision> findByTipoAndRecursoId(String tipo, Long recursoId);
}
