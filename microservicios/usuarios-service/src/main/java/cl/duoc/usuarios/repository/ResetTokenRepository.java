package cl.duoc.usuarios.repository;

import cl.duoc.usuarios.model.ResetToken;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ResetTokenRepository extends JpaRepository<ResetToken, String> {
  void deleteByUsuarioId(Long id);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select r from ResetToken r where r.hash=:hash")
  Optional<ResetToken> locked(@Param("hash") String hash);
}
