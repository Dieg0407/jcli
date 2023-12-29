package dieg0407.tools.jcli.services.dependencies;

import dieg0407.tools.jcli.models.Dependency;
import java.util.Optional;

public interface VersionResolver {

  Optional<Dependency> queryLatestVersion(String artifactId, String groupId);
}
