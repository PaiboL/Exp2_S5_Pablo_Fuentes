package cl.duoc.foro.model;

import jakarta.persistence.*;

@Entity
@Table(name = "fo_comentarios")
public class Comentario {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fo_com_gen")
  @SequenceGenerator(
      name = "fo_com_gen",
      sequenceName = "fo_comentario_seq",
      allocationSize = 1,
      initialValue = 100)
  private Long id;

  @Lob
  @Column(nullable = false)
  private String contenido;

  @Column(name = "autor_id", nullable = false)
  private Long autorId;

  @ManyToOne(optional = false)
  @JoinColumn(name = "tema_id", nullable = false)
  private Tema tema;

  @Column(name = "creado_en", nullable = false)
  private java.time.LocalDateTime creadoEn;

  @Column(nullable = false, columnDefinition = "NUMBER(1,0) default 0")
  @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
  private boolean bloqueado;

  public boolean isBloqueado() {
    return bloqueado;
  }

  public void setBloqueado(boolean b) {
    bloqueado = b;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long value) {
    this.id = value;
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

  public Tema getTema() {
    return tema;
  }

  public void setTema(Tema value) {
    this.tema = value;
  }

  public java.time.LocalDateTime getCreadoEn() {
    return creadoEn;
  }

  public void setCreadoEn(java.time.LocalDateTime value) {
    this.creadoEn = value;
  }
}
