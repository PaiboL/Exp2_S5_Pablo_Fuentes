package cl.duoc.usuarios.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "us_reset_tokens")
public class ResetToken {
  @Id
  @Column(length = 64)
  private String hash;

  @ManyToOne(optional = false)
  @JoinColumn(name = "usuario_id")
  private Usuario usuario;

  @Column(nullable = false)
  private Instant expira;

  public String getHash() {
    return hash;
  }

  public void setHash(String v) {
    hash = v;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario v) {
    usuario = v;
  }

  public Instant getExpira() {
    return expira;
  }

  public void setExpira(Instant v) {
    expira = v;
  }
}
