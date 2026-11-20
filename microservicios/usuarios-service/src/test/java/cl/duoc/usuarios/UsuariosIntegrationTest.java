package cl.duoc.usuarios;

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
class UsuariosIntegrationTest {
  @Autowired MockMvc mvc;

  @Test
  void loginReal() throws Exception {
    mvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin@foro.local\",\"password\":\"ForoDemo2026!\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").isString())
        .andExpect(jsonPath("$.usuario.rol").value("ADMIN"))
        .andExpect(jsonPath("$.usuario.passwordHash").doesNotExist());
  }

  @Test
  void passwordIncorrecta() throws Exception {
    mvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin@foro.local\",\"password\":\"incorrecta\"}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void registroSiempreMember() throws Exception {
    mvc.perform(
            post("/api/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"nombre\":\"Prueba\",\"email\":\"nuevo@foro.local\",\"password\":\"Password123!\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.rol").value("MEMBER"));
  }

  @Test
  void rechazaEscaladaEnRegistro() throws Exception {
    mvc.perform(
            post("/api/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"nombre\":\"Prueba\",\"email\":\"nuevo@foro.local\",\"password\":\"Password123!\",\"rol\":\"ADMIN\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void duplicado() throws Exception {
    mvc.perform(
            post("/api/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"nombre\":\"Prueba\",\"email\":\"ADMIN@foro.local\",\"password\":\"Password123!\"}"))
        .andExpect(status().isConflict());
  }

  @Test
  void validacion() throws Exception {
    mvc.perform(
            post("/api/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"\",\"email\":\"incorrecto\",\"password\":\"x\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.details").isArray());
  }

  @Test
  void sinToken() throws Exception {
    mvc.perform(get("/api/usuarios")).andExpect(status().isUnauthorized());
  }

  @Test
  void memberNoListaUsuarios() throws Exception {
    mvc.perform(
            get("/api/usuarios")
                .with(
                    jwt()
                        .jwt(j -> j.subject("3"))
                        .authorities(new SimpleGrantedAuthority("ROLE_MEMBER"))))
        .andExpect(status().isForbidden());
  }

  @Test
  void adminLeeSinHash() throws Exception {
    mvc.perform(
            get("/api/usuarios/3")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.passwordHash").doesNotExist());
  }

  @Test
  void memberNoLeeOtro() throws Exception {
    mvc.perform(
            get("/api/usuarios/1")
                .with(
                    jwt()
                        .jwt(j -> j.subject("3"))
                        .authorities(new SimpleGrantedAuthority("ROLE_MEMBER"))))
        .andExpect(status().isForbidden());
  }

  @Test
  void adminNoSeElimina() throws Exception {
    mvc.perform(
            delete("/api/usuarios/1")
                .with(
                    jwt()
                        .jwt(j -> j.subject("1"))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isConflict());
  }

  @Test
  void contrasenaUnicodeExcesiva() throws Exception {
    String body =
        new com.fasterxml.jackson.databind.ObjectMapper()
            .writeValueAsString(
                java.util.Map.of(
                    "nombre", "Prueba", "email", "unicode@foro.local", "password", "é".repeat(40)));
    mvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest());
  }
}
