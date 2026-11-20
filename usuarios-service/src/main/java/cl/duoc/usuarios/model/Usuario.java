package cl.duoc.usuarios.model;

import jakarta.persistence.*;

@Entity
@Table(name = "us_usuarios")
public class Usuario {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "us_usuario_gen")
  @SequenceGenerator(
      name = "us_usuario_gen",
      sequenceName = "us_usuario_seq",
      allocationSize = 1,
      initialValue = 100)
  private Long id;

  @Column(nullable = false, length = 100)
  private String nombre;

  @Column(nullable = false, unique = true, length = 150)
  private String email;

  @Column(name = "password_hash", nullable = false, length = 100)
  private String passwordHash;

  @ManyToOne(optional = false)
  @JoinColumn(name = "rol_id", nullable = false)
  private Rol rol;

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

  public String getEmail() {
    return email;
  }

  public void setEmail(String value) {
    this.email = value;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String value) {
    this.passwordHash = value;
  }

  public Rol getRol() {
    return rol;
  }

  public void setRol(Rol value) {
    this.rol = value;
  }
}
