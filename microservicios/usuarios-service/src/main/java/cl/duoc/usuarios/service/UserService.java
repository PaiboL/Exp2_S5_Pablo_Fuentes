package cl.duoc.usuarios.service;

import cl.duoc.usuarios.dto.UserDtos.*;
import cl.duoc.usuarios.error.ApiException;
import cl.duoc.usuarios.model.*;
import cl.duoc.usuarios.repository.*;
import java.time.Instant;
import java.util.*;
import org.slf4j.*;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
  private final UsuarioRepository users;
  private final RolRepository roles;
  private final PasswordEncoder passwords;
  private final JwtEncoder encoder;
  private final ResetTokenRepository resets;
  private final Logger log = LoggerFactory.getLogger(UserService.class);

  public UserService(
      UsuarioRepository u,
      RolRepository r,
      PasswordEncoder p,
      JwtEncoder e,
      ResetTokenRepository resets) {
    users = u;
    roles = r;
    passwords = p;
    encoder = e;
    this.resets = resets;
  }

  private String email(String e) {
    return e.trim().toLowerCase(Locale.ROOT);
  }

  private Vista view(Usuario u) {
    return new Vista(u.getId(), u.getNombre(), u.getEmail(), u.getRol().getNombre());
  }

  private Usuario find(Long id) {
    return users.findById(id).orElseThrow(() -> new ApiException(404, "Usuario no encontrado"));
  }

  public Vista register(Registro r) {
    PasswordPolicy.validate(r.password());
    if (r.password().getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72)
      throw new ApiException(400, "La contrasena excede 72 bytes UTF-8");
    String mail = email(r.email());
    if (users.existsByEmail(mail)) throw new ApiException(409, "Email ya registrado");
    var u = new Usuario();
    u.setNombre(r.nombre().trim());
    u.setEmail(mail);
    u.setPasswordHash(passwords.encode(r.password()));
    u.setRol(
        roles
            .findByNombre("MEMBER")
            .orElseThrow(() -> new ApiException(500, "Catalogo de roles no inicializado")));
    u = users.saveAndFlush(u);
    log.info("Usuario creado id={}", u.getId());
    return view(u);
  }

  @Transactional(readOnly = true)
  public Token login(Login r) {
    var u =
        users
            .findByEmail(email(r.email()))
            .orElseThrow(() -> new ApiException(401, "Credenciales incorrectas"));
    if (r.password().getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72
        || !passwords.matches(r.password(), u.getPasswordHash()))
      throw new ApiException(401, "Credenciales incorrectas");
    var now = Instant.now();
    var claims =
        JwtClaimsSet.builder()
            .issuer("foro-usuarios")
            .subject(u.getId().toString())
            .issuedAt(now)
            .expiresAt(now.plusSeconds(900))
            .claim("roles", List.of(u.getRol().getNombre()))
            .build();
    String jwt =
        encoder
            .encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
            .getTokenValue();
    return new Token(jwt, "Bearer", 900, view(u));
  }

  @Transactional(readOnly = true)
  public Vista get(Long id) {
    return view(find(id));
  }

  @Transactional(readOnly = true)
  public Page<Vista> all(int page, int size) {
    checkPage(page, size);
    return users.findAll(PageRequest.of(page, size, Sort.by("id"))).map(this::view);
  }

  @Transactional(readOnly = true)
  public List<String> roles() {
    return roles.findAll().stream().map(Rol::getNombre).toList();
  }

  public Vista update(Long id, Actualizar r) {
    var u = find(id);
    String mail = email(r.email());
    if (!mail.equals(u.getEmail()) && users.existsByEmail(mail))
      throw new ApiException(409, "Email ya registrado");
    u.setNombre(r.nombre().trim());
    u.setEmail(mail);
    return view(users.saveAndFlush(u));
  }

  public Vista role(Long id, CambiarRol r, Long actor) {
    if (id.equals(actor)) throw new ApiException(409, "No puedes cambiar tu propio rol");
    var u = find(id);
    u.setRol(roles.findByNombre(r.rol()).orElseThrow(() -> new ApiException(400, "Rol invalido")));
    return view(users.save(u));
  }

  public void delete(Long id, Long actor) {
    if (id.equals(actor))
      throw new ApiException(409, "No puedes eliminar tu propia cuenta administradora");
    resets.deleteByUsuarioId(id);
    users.delete(find(id));
    log.info("Usuario eliminado id={}", id);
  }

  private void checkPage(int page, int size) {
    if (page < 0 || size < 1 || size > 100)
      throw new ApiException(400, "page >= 0; size entre 1 y 100");
  }
}
