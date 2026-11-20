package cl.duoc.foro.model;

import jakarta.persistence.*;

@Entity
@Table(name = "fo_temas")
public class Tema {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fo_tema_gen")
  @SequenceGenerator(
      name = "fo_tema_gen",
      sequenceName = "fo_tema_seq",
      allocationSize = 1,
      initialValue = 100)
  private Long id;

  @Column(nullable = false, length = 150)
  private String titulo;

  @Lob
  @Column(nullable = false)
  private String contenido;

  @Column(name = "autor_id", nullable = false)
  private Long autorId;

  @ManyToOne(optional = false)
  @JoinColumn(name = "categoria_id", nullable = false)
  private Categoria categoria;

  @Column(name = "creado_en", nullable = false)
  private java.time.LocalDateTime creadoEn;

  public Long getId() {
    return id;
  }

  public void setId(Long value) {
    this.id = value;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String value) {
    this.titulo = value;
  }

  public String getContenido() {
    return contenido;
  }

  public void setContenido(String value) {
    this.contenido = value;
  }

  public Long getAutorId() {
    return autorId;
  }

  public void setAutorId(Long value) {
    this.autorId = value;
  }

  public Categoria getCategoria() {
    return categoria;
  }

  public void setCategoria(Categoria value) {
    this.categoria = value;
  }

  public java.time.LocalDateTime getCreadoEn() {
    return creadoEn;
  }

  public void setCreadoEn(java.time.LocalDateTime value) {
    this.creadoEn = value;
  }
}
