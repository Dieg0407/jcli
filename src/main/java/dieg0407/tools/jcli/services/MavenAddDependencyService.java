package dieg0407.tools.jcli.services;

import dieg0407.tools.jcli.services.dependencies.VersionResolver;
import dieg0407.tools.jcli.services.engines.BuildFile;
import dieg0407.tools.jcli.shared.DependencyType;
import dieg0407.tools.jcli.shared.ProgramCodes;

public class MavenAddDependencyService implements AddDependencyService {
  private final VersionResolver versionResolver;
  private final RequireInput requireInput;
  private final BuildFile buildFile;


  public MavenAddDependencyService(VersionResolver versionResolver, RequireInput requireInput,
      BuildFile buildFile) {
    this.versionResolver = versionResolver;
    this.requireInput = requireInput;
    this.buildFile = buildFile;
  }

  @Override
  public int add(DependencyType type, String artifactId, String groupId, String version,
      boolean autoAccept) {
    if (buildFile.dependencyExists(artifactId, groupId, version)) {
      return ProgramCodes.SUCCESS;
    }
    final var dependencies = versionResolver.queryVersions(artifactId, groupId, version);
    if (dependencies.isEmpty()) {
      return ProgramCodes.DEPENDENCY_NOT_FOUND;
    }
    if (dependencies.size() == 1 || autoAccept) {
      final var dependency = dependencies.get(0);
      buildFile.addDependency(type, dependency.artifactId(), dependency.groupId(), dependency.version());

      return ProgramCodes.SUCCESS;
    }

    final var dependency = requireInput.choose(dependencies);
    buildFile.addDependency(type, dependency.artifactId(), dependency.groupId(), dependency.version());

    return ProgramCodes.SUCCESS;
  }
}
