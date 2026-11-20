package cl.duoc.usuarios.service;

import cl.duoc.usuarios.error.ApiException;
import cl.duoc.usuarios.model.ResetToken;
import cl.duoc.usuarios.repository.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.Instant;
import java.util.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RecoveryService {
  private final UsuarioRepository users;
  private final ResetTokenRepository tokens;
  private final RecoverySender sender;
  private final PasswordEncoder passwords;

  public RecoveryService(
      UsuarioRepository u, ResetTokenRepository t, RecoverySender s, PasswordEncoder p) {
    users = u;
    tokens = t;
    sender = s;
    passwords = p;
  }

  public static String digest(String token) {
    try {
      return HexFormat.of()
          .formatHex(
              MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  public void request(String email) {
    users
        .findByEmail(email.trim().toLowerCase(Locale.ROOT))
        .ifPresent(
            u -> {
              tokens.deleteByUsuarioId(u.getId());
              tokens.flush();
              byte[] bytes = new byte[32];
              new SecureRandom().nextBytes(bytes);
              String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
              var r = new ResetToken();
              r.setHash(digest(token));
              r.setUsuario(u);
              r.setExpira(Instant.now().plusSeconds(900));
              tokens.save(r);
              sender.send(u.getEmail(), token);
            });
  }

  public void reset(String token, String password) {
    PasswordPolicy.validate(password);
    var r =
        tokens
            .locked(digest(token))
            .filter(t -> t.getExpira().isAfter(Instant.now()))
            .orElseThrow(() -> new ApiException(400, "Código inválido o vencido"));
    r.getUsuario().setPasswordHash(passwords.encode(password));
    users.save(r.getUsuario());
    tokens.delete(r);
  }
}
