package cl.duoc.moderacion.service;

import cl.duoc.moderacion.error.ApiException;
import cl.duoc.moderacion.model.Decision;
import cl.duoc.moderacion.repository.DecisionRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ModerationService {
  private final DecisionRepository repo;
  private final ContentClient content;

  public ModerationService(DecisionRepository r, ContentClient c) {
    repo = r;
    content = c;
  }

  @Transactional(readOnly = true)
  public List<Decision> all() {
    return repo.findAll(Sort.by("id").descending());
  }

  @Transactional(readOnly = true)
  public Decision get(Long id) {
    return repo.findById(id).orElseThrow(() -> new ApiException(404, "Decisión no encontrada"));
  }

  public Decision create(String tipo, Long recurso, String motivo, Long actor, String token) {
    var d = repo.findByTipoAndRecursoId(tipo, recurso).orElse(new Decision());
    d.tipo = tipo;
    d.recursoId = recurso;
    return apply(d, motivo, true, actor, token);
  }

  public Decision update(Long id, String motivo, boolean bloqueado, Long actor, String token) {
    return apply(get(id), motivo, bloqueado, actor, token);
  }

  private Decision apply(Decision d, String motivo, boolean bloqueado, Long actor, String token) {
    content.apply(d.tipo, d.recursoId, bloqueado, token);
    d.motivo = motivo.trim();
    d.bloqueado = bloqueado;
    d.actorId = actor;
    d.fecha = LocalDateTime.now();
    return repo.saveAndFlush(d);
  }

  public void delete(Long id, String token) {
    var d = get(id);
    content.apply(d.tipo, d.recursoId, false, token);
    repo.delete(d);
  }
}
