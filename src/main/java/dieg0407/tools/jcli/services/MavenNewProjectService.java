package dieg0407.tools.jcli.services;

import static java.lang.String.format;

import dieg0407.tools.jcli.services.templates.TemplateReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MavenNewProjectService implements NewProjectService {

  public static final String MAVEN_TEMPLATES = "maven-templates";

  private final TemplateReader templateReader;

  public MavenNewProjectService(TemplateReader reader) {
    this.templateReader = reader;
  }

  public static NewProjectService getInstance() {
    return new MavenNewProjectService(new TemplateReader() {
    });
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
    try {
      final var pom = templateReader.readTemplate(
              format("/%s/%s", MAVEN_TEMPLATES, CONSOLE_APP_TEMPLATE_NAME))
          .replace("${artifactId}", artifactId)
          .replace("${groupId}", groupId)
          .replace("${version}", version)
          .replace("${junit5Version}", "5.7.0")
          .replace("${javaVersion}", "21");

      Files.writeString(Path.of(artifactId, "pom.xml"), pom);
      final var srcDir = Path.of(artifactId, "src", "main", "java", groupId.replace(".", "/"));
      final var srcResourceDir = Path.of(artifactId, "src", "main", "resources");
      final var testDir = Path.of(artifactId, "src", "test", "java", groupId.replace(".", "/"));
      final var testResourceDir = Path.of(artifactId, "src", "test", "resources");

      srcDir.toFile().mkdirs();
      srcResourceDir.toFile().mkdirs();
      testDir.toFile().mkdirs();
      testResourceDir.toFile().mkdirs();

      final var mainClass = templateReader.readTemplate(MAIN_CLASS_TEMPLATE)
          .replace("${package}", groupId)
          .replace("${className}", "ConsoleApplication");

      Files.writeString(Path.of(srcDir.toString(), "ConsoleApplication.java"), mainClass);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
