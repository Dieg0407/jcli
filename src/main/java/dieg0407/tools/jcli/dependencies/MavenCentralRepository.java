package dieg0407.tools.jcli.dependencies;

import dieg0407.tools.jcli.dependencies.api.MavenCentralApi;
import dieg0407.tools.jcli.dependencies.api.MavenCentralDependency;
import dieg0407.tools.jcli.dependencies.models.Dependency;
import java.util.Optional;

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

  private Dependency toDependency(MavenCentralDependency rawDependency) {
    return new Dependency(rawDependency.getA(), rawDependency.getG(),
        rawDependency.getLatestVersion());
  }
}
