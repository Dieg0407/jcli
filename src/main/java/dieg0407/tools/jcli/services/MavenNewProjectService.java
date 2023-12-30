package dieg0407.tools.jcli.services;

import static java.lang.String.format;

import com.fasterxml.jackson.databind.ObjectMapper;
import dieg0407.tools.jcli.directory.FileHandler;
import dieg0407.tools.jcli.directory.FileHandler.Result;
import dieg0407.tools.jcli.services.dependencies.MavenCentralRepository;
import dieg0407.tools.jcli.services.dependencies.VersionResolver;
import dieg0407.tools.jcli.services.dependencies.api.MavenCentralApiImpl;
import dieg0407.tools.jcli.services.templates.TemplateReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MavenNewProjectService implements NewProjectService {

  public static final String MAVEN_TEMPLATES = "maven-templates";

  private final TemplateReader templateReader;
  private final VersionResolver versionResolver;
  private final FileHandler fileHandler;

  public MavenNewProjectService(TemplateReader templateReader, VersionResolver versionResolver,
      FileHandler fileHandler) {
    this.templateReader = templateReader;
    this.versionResolver = versionResolver;
    this.fileHandler = fileHandler;
  }

  public static NewProjectService getInstance() {
    final var api = new MavenCentralApiImpl(new ObjectMapper());
    final var resolver = new MavenCentralRepository(api);
    final var templateReader = new TemplateReader() {};
    final var fileHandler = new FileHandler() {};
    return new MavenNewProjectService(templateReader, resolver, fileHandler);
  }

  @Override
  public void createConsoleApp(String artifactId, String groupId, String version) {
    System.err.println("Creating a new Maven project...");
    final var workdir = Path.of(artifactId).toFile();
    final var result = fileHandler.createFolder(workdir);
    if (result == Result.ERROR || result == null) {
      System.err.println("Unable to create the directory " + workdir.getAbsolutePath());
      return;
    }
    if (result == Result.ALREADY_EXISTS) {
      System.err.println("The directory " + workdir.getAbsolutePath() + " already exists.");
      return;
    }

    createPom(artifactId, groupId, version);
  }

  private void createPom(String artifactId, String groupId, String version) {
    try {
      // fetch junit latest version
      final var junit5 = versionResolver.queryLatestVersion("junit-jupiter", "org.junit.jupiter");
      if (junit5.isEmpty()) {
        throw new RuntimeException("Unable to fetch junit5 latest version");
      }

      final var pom = templateReader.readTemplate(
              format("/%s/%s", MAVEN_TEMPLATES, CONSOLE_APP_TEMPLATE_NAME))
          .replace("${artifactId}", artifactId)
          .replace("${groupId}", groupId)
          .replace("${version}", version)
          .replace("${junit5Version}", junit5.get().version())
          .replace("${javaVersion}", "17");

      fileHandler.writeToFile(Path.of(artifactId, "pom.xml"), pom);
      final var srcDir = Path.of(artifactId, "src", "main", "java", groupId.replace(".", "/"));
      final var srcResourceDir = Path.of(artifactId, "src", "main", "resources");
      final var testDir = Path.of(artifactId, "src", "test", "java", groupId.replace(".", "/"));
      final var testResourceDir = Path.of(artifactId, "src", "test", "resources");

      fileHandler.createFolder(srcDir.toFile());
      fileHandler.createFolder(srcResourceDir.toFile());
      fileHandler.createFolder(testDir.toFile());
      fileHandler.createFolder(testResourceDir.toFile());

      final var mainClass = templateReader.readTemplate(MAIN_CLASS_TEMPLATE)
          .replace("${package}", groupId)
          .replace("${className}", "ConsoleApplication");

      fileHandler.writeToFile(Path.of(srcDir.toString(), "ConsoleApplication.java"), mainClass);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
