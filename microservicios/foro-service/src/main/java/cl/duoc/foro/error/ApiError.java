package cl.duoc.foro.error;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/** Builder de respuesta inmutable, aplicado por el manejador global. */
public record ApiError(
    Instant timestamp, int status, String error, List<Map<String, String>> details) {
  public ApiError {
    if (timestamp == null || status < 400 || status > 599 || error == null || error.isBlank())
      throw new IllegalArgumentException("Error incompleto");
    details = details.stream().map(Map::copyOf).toList();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private int status;
    private String error;
    private List<Map<String, String>> details = List.of();

    private Builder() {}

    public Builder status(int v) {
      status = v;
      return this;
    }

    public Builder error(String v) {
      error = v;
      return this;
    }

    public Builder details(List<Map<String, String>> v) {
      details = v.stream().map(Map::copyOf).toList();
      return this;
    }

    public ApiError build() {
      return new ApiError(Instant.now(), status, error, details);
    }
  }
}
