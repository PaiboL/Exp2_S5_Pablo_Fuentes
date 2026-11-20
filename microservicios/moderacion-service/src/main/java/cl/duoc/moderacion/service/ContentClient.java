package cl.duoc.moderacion.service;

import cl.duoc.moderacion.error.ApiException;
import java.time.Duration;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

@Component
public class ContentClient {
  private final RestClient client;

  public ContentClient(@Value("${app.foro-url}") String url) {
    var f = new SimpleClientHttpRequestFactory();
    f.setConnectTimeout(Duration.ofSeconds(3));
    f.setReadTimeout(Duration.ofSeconds(5));
    client = RestClient.builder().baseUrl(url).requestFactory(f).build();
  }

  public void apply(String tipo, Long id, boolean blocked, String token) {
    try {
      client
          .put()
          .uri("/api/contenido-admin/{tipo}/{id}", tipo, id)
          .header("Authorization", token)
          .body(Map.of("bloqueado", blocked))
          .retrieve()
          .toBodilessEntity();
    } catch (HttpClientErrorException e) {
      throw new ApiException(e.getStatusCode().value(), "El foro rechazó el cambio de moderación");
    } catch (RestClientException e) {
      throw new ApiException(
          503, "El servicio de foro no está disponible. Reintenta la misma operación.");
    }
  }
}
