package dieg0407.tools.jcli.services.engines.mvn;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import dieg0407.tools.jcli.services.engines.BuildFile;
import dieg0407.tools.jcli.services.engines.InvalidBuildFileException;
import dieg0407.tools.jcli.shared.DependencyType;
import jakarta.annotation.Nullable;
import java.io.IOException;
import java.util.Optional;

public class PomBuildFile implements BuildFile {
  private final PomHandler pomHandler;
  private final XmlMapper xmlMapper;

  public PomBuildFile(PomHandler pomHandler, XmlMapper xmlMapper) {
    this.pomHandler = pomHandler;
    this.xmlMapper = xmlMapper;
  }

  @Override
  public boolean dependencyExists(String artifactId, @Nullable String groupId, @Nullable String version) throws InvalidBuildFileException {
    final var pomFile = pomHandler.fetch();
    try {
      final var root = xmlMapper.readTree(pomFile);
      final var dependenciesNode = Optional.ofNullable(root.get("dependencies"))
          .map(dependencies -> dependencies.get("dependency"))
          .orElse(null);

      if (dependenciesNode == null || !dependenciesNode.isArray()) {
        throw new InvalidBuildFileException("No dependencies node under the project. Invalid pom.xml file");
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
      String version) {
    final var pomFile = pomHandler.fetch();
    try {
      final var root = xmlMapper.readTree(pomFile);
      if (root.get("dependencies") == null) {
        throw new InvalidBuildFileException("No dependencies node under the project. Invalid pom.xml file");
      }
      final var dependencies = (ArrayNode) root.get("dependencies").get("dependency");
      for(var dependency : dependencies) {
        if (!dependency.get("artifactId").asText().equals(artifactId)
            || !dependency.get("groupId").asText().equals(groupId)) {
          continue;
        }
        final var editableDependency = (ObjectNode) dependency;
        editableDependency.put("version", version);
        if (type != DependencyType.compile) {
          editableDependency.put("scope", type.toString());
        }
        pomHandler.save(xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(root));
        return;
      }
      final var dependencyNode = xmlMapper.createObjectNode();
      dependencyNode.put("artifactId", artifactId);
      dependencyNode.put("groupId", groupId);
      dependencyNode.put("version", version);

      dependencies.add(dependencyNode);

      pomHandler.save(xmlMapper.writeValueAsString(root));
    } catch (IOException e) {
      throw new InvalidBuildFileException("Error parsing pom.xml file", e);
    }
  }
}
