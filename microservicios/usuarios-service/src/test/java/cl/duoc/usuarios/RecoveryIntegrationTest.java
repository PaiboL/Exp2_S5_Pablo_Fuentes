package cl.duoc.usuarios;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cl.duoc.usuarios.error.ApiException;
import cl.duoc.usuarios.repository.*;
import cl.duoc.usuarios.service.*;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("demo")
@Transactional
class RecoveryIntegrationTest {
  @Autowired RecoveryService service;
  @Autowired ResetTokenRepository tokens;
  @Autowired UsuarioRepository users;
  @Autowired PasswordEncoder encoder;
  @MockitoBean RecoverySender sender;

  String request() {
    service.request("miembro@foro.local");
    var c = ArgumentCaptor.forClass(String.class);
    verify(sender).send(eq("miembro@foro.local"), c.capture());
    return c.getValue();
  }

  @Test
  void codigoHasheadoUnSoloUso() {
    var token = request();
    assertEquals(43, token.length());
    assertFalse(tokens.existsById(token));
    assertTrue(tokens.existsById(RecoveryService.digest(token)));
    service.reset(token, "NuevaClave2026!");
    assertTrue(
        encoder.matches(
            "NuevaClave2026!",
            users.findByEmail("miembro@foro.local").orElseThrow().getPasswordHash()));
    assertThrows(ApiException.class, () -> service.reset(token, "OtraClave2026!"));
  }

  @Test
  void codigoVencido() {
    var token = request();
    var t = tokens.findById(RecoveryService.digest(token)).orElseThrow();
    t.setExpira(Instant.now().minusSeconds(1));
    tokens.saveAndFlush(t);
    assertThrows(ApiException.class, () -> service.reset(token, "NuevaClave2026!"));
  }

  @Test
  void cuentaInexistenteNoEnviaCorreo() {
    service.request("ausente@foro.local");
    verifyNoInteractions(sender);
    assertEquals(0, tokens.count());
  }

  @Test
  void politicaExigeTodasLasReglas() {
    for (var s :
        new String[] {
          "shortA1!",
          "sinmayuscula1!",
          "SINMINUSCULA1!",
          "SinNumeros!!!",
          "SinSimbolo123",
          "A1!" + "á".repeat(40)
        }) assertThrows(ApiException.class, () -> PasswordPolicy.validate(s));
    assertDoesNotThrow(() -> PasswordPolicy.validate("Correcta2026!"));
  }
}
