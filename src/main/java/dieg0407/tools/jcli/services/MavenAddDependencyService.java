package dieg0407.tools.jcli.services;

import dieg0407.tools.jcli.shared.DependencyType;

public class MavenAddDependencyService implements AddDependencyService {

  @Override
  public int add(DependencyType type, String artifactId, String groupId, String version,
      boolean autoAccept) {
    return 0;
  }
}
