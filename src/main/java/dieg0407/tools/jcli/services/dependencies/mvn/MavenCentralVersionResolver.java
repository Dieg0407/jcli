package dieg0407.tools.jcli.services.dependencies.mvn;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dieg0407.tools.jcli.services.dependencies.VersionResolver;
import dieg0407.tools.jcli.services.dependencies.models.Dependency;
import jakarta.annotation.Nullable;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

public class MavenCentralVersionResolver implements VersionResolver {

  private final ObjectMapper mapper;

  public MavenCentralVersionResolver(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public Optional<Dependency> queryLatestVersion(String artifactId, String groupId) {
    return queryMavenCentral(artifactId, groupId, null).stream()
        .findFirst()
        .map(this::toDependency);
  }

  @Override
  public List<Dependency> query(String artifactId, @Nullable String groupId,
      @Nullable String version) {
    return queryMavenCentral(artifactId, groupId, version).stream()
        .map(this::toDependency)
        .toList();
  }

  private Dependency toDependency(MavenCentralDependency rawDependency) {
    return new Dependency(rawDependency.getA(), rawDependency.getG(),
        rawDependency.getLatestVersion() == null ? rawDependency.getV() : rawDependency.getLatestVersion());
  }

  private List<MavenCentralDependency> queryMavenCentral(String artifactId, @Nullable String groupId, @Nullable String version) {
    try {
      final var client = HttpClient.newHttpClient();
      final var query = generateQuery(artifactId, groupId, version);
      final var request = HttpRequest.newBuilder()
          .GET()
          .uri(URI.create("https://search.maven.org/solrsearch/select" + query))
          .build();
      final var result = client.send(request, HttpResponse.BodyHandlers.ofString());
      if (result.statusCode() != 200) {
        throw new RuntimeException(
            "Error while querying maven central: " + result.body() + " with status code: "
                + result.statusCode());
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

  private String generateQuery(String artifactId, String groupId, String version) {
    final var builder = new StringBuilder("?q=a:");
    builder.append(artifactId);
    if (groupId != null) {
      builder.append("+AND+g:").append(groupId);
    }
    if (version != null) {
      builder.append("+AND+v:").append(version);
    }
    return builder.append("&rows=20&wt=json").toString();
  }
}
