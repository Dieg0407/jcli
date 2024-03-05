package dieg0407.tools.jcli.services.dependencies;

import dieg0407.tools.jcli.services.dependencies.models.Dependency;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public interface VersionResolver {

  Optional<Dependency> queryLatestVersion(String artifactId, String groupId);

  List<Dependency> query(String artifactId, @Nullable String groupId, @Nullable String version);
}
