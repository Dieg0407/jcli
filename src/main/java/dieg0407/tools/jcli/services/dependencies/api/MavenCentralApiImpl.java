package dieg0407.tools.jcli.services.dependencies.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class MavenCentralApiImpl implements MavenCentralApi {

  private final ObjectMapper mapper;

  public MavenCentralApiImpl(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public List<MavenCentralDependency> query(String artifactId, String groupId) {
    try {
      final var client = HttpClient.newHttpClient();
      final var query = String.format("?q=a:%s+AND+g:%s&rows=20&wt=json", artifactId, groupId);
      final var request = HttpRequest.newBuilder()
          .GET()
          .uri(URI.create("https://search.maven.org/solrsearch/select" + query))
          .build();
      final var result = client.send(request, HttpResponse.BodyHandlers.ofString());
      if (result.statusCode() != 200) {
        throw new RuntimeException("Error while querying maven central: " + result.body() + " with status code: " + result.statusCode());
      }
      final var content = result.body();
      final var response = mapper.readTree(content);
      final var node = response.get("response").get("docs");

      return mapper.readValue(node.toString(), new TypeReference<>() {
      });
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
