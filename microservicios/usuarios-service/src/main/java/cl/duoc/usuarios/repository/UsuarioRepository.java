package cl.duoc.usuarios.repository;

import cl.duoc.usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
  java.util.Optional<Usuario> findByEmail(String email);

  boolean existsByEmail(String email);
}
