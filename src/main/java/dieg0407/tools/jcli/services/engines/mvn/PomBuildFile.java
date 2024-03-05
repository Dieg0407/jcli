package dieg0407.tools.jcli.services.engines.mvn;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import dieg0407.tools.jcli.services.engines.BuildFile;
import dieg0407.tools.jcli.services.engines.InvalidBuildFileException;
import dieg0407.tools.jcli.shared.DependencyType;
import jakarta.annotation.Nullable;
import java.io.IOException;

public class PomBuildFile implements BuildFile {
  private final PomFetcher pomFetcher;
  private final XmlMapper xmlMapper;

  public PomBuildFile(PomFetcher pomFetcher, XmlMapper xmlMapper) {
    this.pomFetcher = pomFetcher;
    this.xmlMapper = xmlMapper;
  }

  @Override
  public boolean dependencyExists(String artifactId, @Nullable String groupId, @Nullable String version) throws InvalidBuildFileException {
    final var pomFile = pomFetcher.fetch();
    try {
      final var xml = xmlMapper.readTree(pomFile);
      final var projectNode = xml.get("project");
      if (projectNode == null) {
        throw new InvalidBuildFileException("No project node. Invalid pom.xml file");
      }
      final var dependenciesNode = projectNode.get("dependencies");
      if (dependenciesNode == null || !dependenciesNode.isArray()) {
        throw new InvalidBuildFileException("No dependencies under project. Invalid pom.xml file");
      }
      for (var dependencyNode : dependenciesNode) {
        final var dependencyArtifactId = dependencyNode.get("artifactId");
        final var dependencyGroupId = dependencyNode.get("groupId");
        final var dependencyVersion = dependencyNode.get("version");

        if (dependencyArtifactId == null || dependencyGroupId == null) {
          continue;
        }
        if (!dependencyArtifactId.asText().equals(artifactId)
            || !dependencyGroupId.asText().equals(groupId)) {
          continue;
        }
        if (version == null) {
          return true;
        }
        if (dependencyVersion == null) {
          continue;
        }
        if (dependencyVersion.asText().equals(version)) {
          return true;
        }
      }
    } catch (IOException e) {
      throw new InvalidBuildFileException("Error parsing pom.xml file", e);
    }
    return false;
  }

  @Override
  public void addDependency(DependencyType type, String artifactId, String groupId,
      @Nullable String version) {

  }
}
