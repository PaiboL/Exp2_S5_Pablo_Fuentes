package cl.duoc.foro.model;

import jakarta.persistence.*;

@Entity
@Table(name = "fo_categorias")
public class Categoria {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fo_cat_gen")
  @SequenceGenerator(
      name = "fo_cat_gen",
      sequenceName = "fo_categoria_seq",
      allocationSize = 1,
      initialValue = 100)
  private Long id;

  @Column(nullable = false, unique = true, length = 80)
  private String nombre;

  @Column(nullable = false, length = 250)
  private String descripcion;

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

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String value) {
    this.descripcion = value;
  }
}
