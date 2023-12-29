package dieg0407.tools.jcli.services;

import static java.lang.String.format;

import java.io.IOException;
import java.nio.file.Path;

public class MavenNewProjectService implements  NewProjectService {

  public static final String MAVEN_TEMPLATES = "maven-templates";

  public static NewProjectService getInstance() {
    return new MavenNewProjectService();
  }

  @Override
  public void createConsoleApp(String artifactId, String groupId, String version) {
    System.err.println("Creating a new Maven project...");
    final var workdir = Path.of(artifactId).toFile();
    if (!workdir.exists()) {
      workdir.mkdir();
    } else {
      System.err.println("The directory " + workdir.getAbsolutePath() + " already exists.");
    }
    createPom(artifactId, groupId, version);
  }

  private void createPom(String artifactId, String groupId, String version) {
    try (final var pomTemplate = getClass().getResourceAsStream(format("/%s/%s", MAVEN_TEMPLATES, CONSOLE_APP_TEMPLATE_NAME))
    ) {
      if (pomTemplate == null) {
        throw new RuntimeException("Pom template for console app not found");
      }
      final var content = new String(pomTemplate.readAllBytes());
      final var pom = content
          .replace("${artifactId}", artifactId)
          .replace("${groupId}", groupId)
          .replace("${version}", version)
          .replace("${javaVersion}", Runtime.version().toString());

      System.err.println("POM: " + pom);
    } catch (IOException e) {
      e.printStackTrace(System.err);
    }
  }
}
