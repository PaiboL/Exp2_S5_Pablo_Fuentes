package cl.duoc.foro;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo")
@Transactional
class ForoIntegrationTest {
  @Autowired MockMvc mvc;

  private org.springframework.test.web.servlet.request.RequestPostProcessor user(
      String id, String role) {
    return jwt().jwt(j -> j.subject(id)).authorities(new SimpleGrantedAuthority("ROLE_" + role));
  }

  @Test
  void cincoCategorias() throws Exception {
    mvc.perform(get("/api/categorias").with(user("3", "MEMBER")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(5));
  }

  @Test
  void buscarPorTituloYCategoria() throws Exception {
    mvc.perform(get("/api/temas?q=spring&categoriaId=1").with(user("3", "MEMBER")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(1));
  }

  @Test
  void paginaInvalida() throws Exception {
    mvc.perform(get("/api/temas?size=101").with(user("3", "MEMBER")))
        .andExpect(status().isBadRequest());
  }

  @Test
  void inexistente() throws Exception {
    mvc.perform(get("/api/temas/99999").with(user("3", "MEMBER"))).andExpect(status().isNotFound());
  }

  @Test
  void memberNoCreaCategoria() throws Exception {
    mvc.perform(
            post("/api/categorias")
                .with(user("3", "MEMBER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Otra\",\"descripcion\":\"Una categoria\"}"))
        .andExpect(status().isForbidden());
  }

  @Test
  void autorNoSuplantable() throws Exception {
    mvc.perform(
            post("/api/temas")
                .with(user("3", "MEMBER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"titulo\":\"Tema\",\"contenido\":\"Texto\",\"categoriaId\":1,\"autorId\":1}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void creaTemaConAutorDelToken() throws Exception {
    mvc.perform(
            post("/api/temas")
                .with(user("3", "MEMBER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"titulo\":\"Tema nuevo\",\"contenido\":\"Texto\",\"categoriaId\":1}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.autorId").value(3));
  }

  @Test
  void noBorraTemaAjeno() throws Exception {
    mvc.perform(delete("/api/temas/1").with(user("100", "MEMBER")))
        .andExpect(status().isForbidden());
  }

  @Test
  void noBorraComentarioAjeno() throws Exception {
    mvc.perform(delete("/api/comentarios/1").with(user("3", "MEMBER")))
        .andExpect(status().isForbidden());
  }

  @Test
  void protegeCategoriaConTemas() throws Exception {
    mvc.perform(delete("/api/categorias/1").with(user("1", "ADMIN")))
        .andExpect(status().isConflict());
  }

  @Test
  void mantieneCincoCategorias() throws Exception {
    mvc.perform(delete("/api/categorias/5").with(user("1", "ADMIN")))
        .andExpect(status().isConflict());
  }

  @Test
  void moderadorBorraTemaYComentarios() throws Exception {
    mvc.perform(delete("/api/temas/1").with(user("2", "MODERATOR")))
        .andExpect(status().isNoContent());
    mvc.perform(get("/api/comentarios/1").with(user("2", "MODERATOR")))
        .andExpect(status().isNotFound());
  }
}
