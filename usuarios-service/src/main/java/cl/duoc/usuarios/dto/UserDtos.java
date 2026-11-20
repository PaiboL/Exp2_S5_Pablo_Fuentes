package cl.duoc.usuarios.dto;

import jakarta.validation.constraints.*;

public class UserDtos {
  public record Registro(
      @NotBlank @Size(max = 100) String nombre,
      @NotBlank @Email @Size(max = 150) String email,
      @NotBlank @Size(min = 10, max = 64) String password) {}

  public record Login(
      @NotBlank @Email @Size(max = 150) String email, @NotBlank @Size(max = 64) String password) {}

  public record Actualizar(
      @NotBlank @Size(max = 100) String nombre, @NotBlank @Email @Size(max = 150) String email) {}

  public record CambiarRol(@NotBlank @Pattern(regexp = "ADMIN|MODERATOR|MEMBER") String rol) {}

  public record Vista(Long id, String nombre, String email, String rol) {}

  public record Token(String accessToken, String tokenType, long expiresIn, Vista usuario) {}
}
