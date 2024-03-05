package dieg0407.tools.jcli.services.dependencies.api;

import jakarta.annotation.Nullable;
import java.util.List;

public interface MavenCentralApi {

  List<MavenCentralDependency> query(String artifactId, @Nullable String groupId, @Nullable String version);
}
