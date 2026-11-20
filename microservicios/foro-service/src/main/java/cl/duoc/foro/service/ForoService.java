package cl.duoc.foro.service;

import cl.duoc.foro.dto.ForoDtos.*;
import cl.duoc.foro.error.ApiException;
import cl.duoc.foro.model.*;
import cl.duoc.foro.repository.*;
import java.time.LocalDateTime;
import java.util.*;
import org.slf4j.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ForoService {
  private final CategoriaRepository categorias;
  private final TemaRepository temas;
  private final ComentarioRepository comentarios;
  private final Logger log = LoggerFactory.getLogger(ForoService.class);

  public ForoService(CategoriaRepository c, TemaRepository t, ComentarioRepository m) {
    categorias = c;
    temas = t;
    comentarios = m;
  }

  private Categoria cat(Long id) {
    return categorias
        .findById(id)
        .orElseThrow(() -> new ApiException(404, "Categoria no encontrada"));
  }

  private Tema tema(Long id) {
    return temas.findById(id).orElseThrow(() -> new ApiException(404, "Tema no encontrado"));
  }

  private Comentario comentario(Long id) {
    return comentarios
        .findById(id)
        .orElseThrow(() -> new ApiException(404, "Comentario no encontrado"));
  }

  private Long actor(Authentication a) {
    return Long.valueOf(a.getName());
  }

  private void owner(Long id, Authentication a) {
    boolean staff =
        a.getAuthorities().stream()
            .anyMatch(x -> Set.of("ROLE_ADMIN", "ROLE_MODERATOR").contains(x.getAuthority()));
    if (!id.equals(actor(a)) && !staff)
      throw new ApiException(
          403, "Solo el autor o el personal autorizado puede modificar este contenido");
  }

  private Pageable page(int p, int s) {
    if (p < 0 || s < 1 || s > 100) throw new ApiException(400, "page >= 0; size entre 1 y 100");
    return PageRequest.of(p, s, Sort.by("id").descending());
  }

  private CategoriaVista cv(Categoria c) {
    return new CategoriaVista(c.getId(), c.getNombre(), c.getDescripcion());
  }

  private TemaVista tv(Tema t) {
    return new TemaVista(
        t.getId(),
        t.getTitulo(),
        t.getContenido(),
        t.getAutorId(),
        t.getCategoria().getId(),
        t.getCategoria().getNombre(),
        t.getCreadoEn(),
        t.isBloqueado());
  }

  private ComentarioVista mv(Comentario c) {
    return new ComentarioVista(
        c.getId(),
        c.getContenido(),
        c.getAutorId(),
        c.getTema().getId(),
        c.getCreadoEn(),
        c.isBloqueado());
  }

  @Transactional(readOnly = true)
  public List<CategoriaVista> categories() {
    return categorias.findAll(Sort.by("id")).stream().map(this::cv).toList();
  }

  @Transactional(readOnly = true)
  public CategoriaVista category(Long id) {
    return cv(cat(id));
  }

  public CategoriaVista saveCategory(Long id, CategoriaInput r) {
    var c = id == null ? new Categoria() : cat(id);
    String n = r.nombre().trim();
    if ((id == null || !c.getNombre().equalsIgnoreCase(n))
        && categorias.existsByNombreIgnoreCase(n))
      throw new ApiException(409, "Categoria duplicada");
    c.setNombre(n);
    c.setDescripcion(r.descripcion().trim());
    return cv(categorias.saveAndFlush(c));
  }

  public void deleteCategory(Long id) {
    var c = cat(id);
    if (temas.existsByCategoriaId(id)) throw new ApiException(409, "La categoria contiene temas");
    if (categorias.count() <= 5)
      throw new ApiException(409, "Deben mantenerse al menos cinco categorias");
    categorias.delete(c);
  }

  @Transactional(readOnly = true)
  public Page<TemaVista> search(String q, Long category, int p, int s) {
    if (q.length() > 150) throw new ApiException(400, "Busqueda demasiado extensa");
    Specification<Tema> spec = (root, query, cb) -> cb.isFalse(root.get("bloqueado"));
    if (!q.isBlank()) {
      String escaped =
          q.toLowerCase(Locale.ROOT).replace("!", "!!").replace("%", "!%").replace("_", "!_");
      spec = spec.and((r, query, b) -> b.like(b.lower(r.get("titulo")), "%" + escaped + "%", '!'));
    }
    if (category != null) {
      cat(category);
      spec = spec.and((r, query, b) -> b.equal(r.get("categoria").get("id"), category));
    }
    return temas.findAll(spec, page(p, s)).map(this::tv);
  }

  @Transactional(readOnly = true)
  public TemaVista getTopic(Long id) {
    var t = tema(id);
    if (t.isBloqueado()) throw new ApiException(404, "Tema no disponible");
    return tv(t);
  }

  public TemaVista saveTopic(Long id, TemaInput r, Authentication a) {
    var t = id == null ? new Tema() : tema(id);
    if (id != null) {
      if (t.isBloqueado()) throw new ApiException(409, "Tema bloqueado");
      owner(t.getAutorId(), a);
    } else {
      t.setAutorId(actor(a));
      t.setCreadoEn(LocalDateTime.now());
    }
    t.setTitulo(r.titulo().trim());
    t.setContenido(r.contenido().trim());
    t.setCategoria(cat(r.categoriaId()));
    t = temas.saveAndFlush(t);
    log.info("Tema guardado id={} actor={}", t.getId(), actor(a));
    return tv(t);
  }

  public void deleteTopic(Long id, Authentication a) {
    var t = tema(id);
    owner(t.getAutorId(), a);
    comentarios.deleteByTemaId(id);
    comentarios.flush();
    temas.delete(t);
    log.info("Tema eliminado id={} actor={}", id, actor(a));
  }

  @Transactional(readOnly = true)
  public Page<ComentarioVista> comments(Long temaId, int p, int s) {
    getTopic(temaId);
    return comentarios.findByTemaIdAndBloqueadoFalse(temaId, page(p, s)).map(this::mv);
  }

  @Transactional(readOnly = true)
  public ComentarioVista getComment(Long id) {
    var c = comentario(id);
    getTopic(c.getTema().getId());
    if (c.isBloqueado()) throw new ApiException(404, "Comentario no disponible");
    return mv(c);
  }

  public ComentarioVista addComment(Long temaId, ComentarioInput r, Authentication a) {
    getTopic(temaId);
    var c = new Comentario();
    c.setTema(tema(temaId));
    c.setAutorId(actor(a));
    c.setContenido(r.contenido().trim());
    c.setCreadoEn(LocalDateTime.now());
    return mv(comentarios.saveAndFlush(c));
  }

  public ComentarioVista updateComment(Long id, ComentarioInput r, Authentication a) {
    var c = comentario(id);
    getComment(id);
    owner(c.getAutorId(), a);
    c.setContenido(r.contenido().trim());
    return mv(comentarios.saveAndFlush(c));
  }

  public void deleteComment(Long id, Authentication a) {
    var c = comentario(id);
    owner(c.getAutorId(), a);
    comentarios.delete(c);
  }

  @Transactional(readOnly = true)
  public List<TemaVista> inspectTopics() {
    return temas.findAll(Sort.by("id").descending()).stream().map(this::tv).toList();
  }

  @Transactional(readOnly = true)
  public List<ComentarioVista> inspectComments() {
    return comentarios.findAll(Sort.by("id").descending()).stream().map(this::mv).toList();
  }

  public void moderate(String tipo, Long id, boolean bloqueado) {
    if (tipo.equals("TEMA")) {
      var t = tema(id);
      t.setBloqueado(bloqueado);
      temas.save(t);
    } else if (tipo.equals("COMENTARIO")) {
      var c = comentario(id);
      c.setBloqueado(bloqueado);
      comentarios.save(c);
    } else throw new ApiException(400, "Tipo inválido");
  }
}
