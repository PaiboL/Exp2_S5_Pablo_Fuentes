package cl.duoc.usuarios.model;

import jakarta.persistence.*;

@Entity
@Table(name = "us_roles")
public class Rol {
  @Id private Long id;

  @Column(nullable = false, unique = true, length = 20)
  private String nombre;

  public Long getId() {
    return id;
  }

  public void setId(Long value) {
    this.id = value;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String value) {
    this.nombre = value;
  }
}
