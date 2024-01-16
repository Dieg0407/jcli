package dieg0407.tools.jcli.services.dependencies;

import dieg0407.tools.jcli.services.dependencies.models.Dependency;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface VersionResolver {

  Optional<Dependency> queryLatestVersion(String artifactId, String groupId);

  List<Dependency> queryVersions(String artifactId, @Nullable String groupId, @Nullable String version);
}
