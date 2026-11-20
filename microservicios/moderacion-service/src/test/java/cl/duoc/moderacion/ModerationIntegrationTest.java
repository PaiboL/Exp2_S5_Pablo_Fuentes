package cl.duoc.moderacion;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cl.duoc.moderacion.error.ApiException;
import cl.duoc.moderacion.repository.DecisionRepository;
import cl.duoc.moderacion.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo")
@Transactional
class ModerationIntegrationTest {
  @Autowired ModerationService service;
  @Autowired DecisionRepository repo;
  @Autowired MockMvc mvc;
  @MockitoBean ContentClient content;

  @Test
  void miembroNoAdmin() throws Exception {
    mvc.perform(
            get("/api/moderaciones")
                .with(
                    jwt()
                        .jwt(j -> j.subject("3"))
                        .authorities(new SimpleGrantedAuthority("ROLE_MEMBER"))))
        .andExpect(status().isForbidden());
  }

  @Test
  void bloqueoIdempotenteYRestauracion() {
    var first = service.create("TEMA", 1L, "Motivo", 1L, "Bearer x");
    var second = service.create("TEMA", 1L, "Reintento", 1L, "Bearer x");
    assertEquals(first.id, second.id);
    assertEquals(1, repo.count());
    service.update(first.id, "Restaurado", false, 1L, "Bearer x");
    verify(content).apply("TEMA", 1L, false, "Bearer x");
    assertFalse(service.get(first.id).bloqueado);
    service.delete(first.id, "Bearer x");
    assertEquals(0, repo.count());
  }

  @Test
  void falloRemotoNoCreaDecision() {
    doThrow(new ApiException(503, "No disponible"))
        .when(content)
        .apply(anyString(), anyLong(), anyBoolean(), anyString());
    assertThrows(ApiException.class, () -> service.create("TEMA", 4L, "Prueba", 1L, "Bearer x"));
    assertEquals(0, repo.count());
  }

  @Test
  void recursoInexistente() {
    assertThrows(ApiException.class, () -> service.get(999L));
  }
}
