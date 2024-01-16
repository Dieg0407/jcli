package dieg0407.tools.jcli.services;

import dieg0407.tools.jcli.shared.DependencyType;
import jakarta.annotation.Nullable;

public interface AddDependencyService {

  int add(DependencyType type, String artifactId, @Nullable String groupId,  @Nullable String version,
      boolean autoAccept);
}
