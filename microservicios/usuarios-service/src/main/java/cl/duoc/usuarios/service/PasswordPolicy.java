package cl.duoc.usuarios.service;

import cl.duoc.usuarios.error.ApiException;
import java.nio.charset.StandardCharsets;

public final class PasswordPolicy {
  private PasswordPolicy() {}

  public static void validate(String p) {
    if (p == null
        || p.length() < 10
        || p.length() > 64
        || p.getBytes(StandardCharsets.UTF_8).length > 72
        || !p.matches(".*[A-Z].*")
        || !p.matches(".*[a-z].*")
        || !p.matches(".*[0-9].*")
        || !p.matches(".*[^a-zA-Z0-9].*"))
      throw new ApiException(
          400,
          "La contraseña requiere 10 a 64 caracteres, mayúscula, minúscula, número y símbolo;"
              + " máximo 72 bytes UTF-8");
  }
}
