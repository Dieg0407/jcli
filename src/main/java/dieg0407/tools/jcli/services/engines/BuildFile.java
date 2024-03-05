package dieg0407.tools.jcli.services.engines;

import dieg0407.tools.jcli.shared.DependencyType;
import jakarta.annotation.Nullable;

public interface BuildFile {
  boolean dependencyExists(String artifactId, @Nullable String groupId, @Nullable String version) throws InvalidBuildFileException;

  void addDependency(DependencyType type, String artifactId, String groupId, @Nullable String version) throws InvalidBuildFileException;

}
