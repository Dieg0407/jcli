package dieg0407.tools.jcli.dependencies;

import dieg0407.tools.jcli.dependencies.models.Dependency;
import java.util.Optional;

public interface VersionResolver {

  Optional<Dependency> queryLatestVersion(String artifactId, String groupId);
}
