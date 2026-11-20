package cl.duoc.moderacion.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "mo_decisiones",
    uniqueConstraints = @UniqueConstraint(columnNames = {"tipo", "recurso_id"}))
public class Decision {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mo_gen")
  @SequenceGenerator(name = "mo_gen", sequenceName = "mo_decision_seq", allocationSize = 1)
  public Long id;

  @Column(nullable = false, length = 20)
  public String tipo;

  @Column(name = "recurso_id", nullable = false)
  public Long recursoId;

  @Column(nullable = false, length = 300)
  public String motivo;

  @Column(nullable = false)
  @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
  public boolean bloqueado;

  @Column(name = "actor_id", nullable = false)
  public Long actorId;

  @Column(nullable = false)
  public LocalDateTime fecha;
}
