package cl.duoc.usuarios.repository;

import cl.duoc.usuarios.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Long> {
  java.util.Optional<Rol> findByNombre(String nombre);
}
