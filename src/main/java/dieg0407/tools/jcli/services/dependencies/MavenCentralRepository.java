package dieg0407.tools.jcli.services.dependencies;

import dieg0407.tools.jcli.services.dependencies.api.MavenCentralApi;
import dieg0407.tools.jcli.services.dependencies.api.MavenCentralDependency;
import dieg0407.tools.jcli.services.dependencies.models.Dependency;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MavenCentralRepository implements VersionResolver {

  private final MavenCentralApi mavenCentralApi;

  public MavenCentralRepository(MavenCentralApi mavenCentralApi) {
    this.mavenCentralApi = mavenCentralApi;
  }

  @Override
  public Optional<Dependency> queryLatestVersion(String artifactId, String groupId) {
    return mavenCentralApi.query(artifactId, groupId).stream()
        .findFirst()
        .map(this::toDependency);
  }

  @Override
  public List<Dependency> queryVersions(String artifactId, @Nullable String groupId,
      @Nullable String version) {
    throw new UnsupportedOperationException();
  }

  private Dependency toDependency(MavenCentralDependency rawDependency) {
    return new Dependency(rawDependency.getA(), rawDependency.getG(),
        rawDependency.getLatestVersion());
  }
}