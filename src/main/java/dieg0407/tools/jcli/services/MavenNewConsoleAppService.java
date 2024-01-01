package dieg0407.tools.jcli.services;

import static dieg0407.tools.jcli.services.LongOperationWrapper.wrap;
import static java.lang.String.format;

import com.fasterxml.jackson.databind.ObjectMapper;
import dieg0407.tools.jcli.dependencies.MavenCentralRepository;
import dieg0407.tools.jcli.dependencies.VersionResolver;
import dieg0407.tools.jcli.dependencies.api.MavenCentralApiImpl;
import dieg0407.tools.jcli.engines.Engine;
import dieg0407.tools.jcli.engines.MavenEngine;
import dieg0407.tools.jcli.services.FileHandler.Result;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class MavenNewConsoleAppService implements NewConsoleAppService {

  public static final String MAVEN_TEMPLATES = "maven-templates";

  private final TemplateReader templateReader;
  private final VersionResolver versionResolver;
  private final FileHandler fileHandler;

  private final Engine engine;

  public MavenNewConsoleAppService(TemplateReader templateReader, VersionResolver versionResolver,
      FileHandler fileHandler, Engine engine) {
    this.templateReader = templateReader;
    this.versionResolver = versionResolver;
    this.fileHandler = fileHandler;
    this.engine = engine;
  }

  public static NewConsoleAppService getInstance() {
    final var api = new MavenCentralApiImpl(new ObjectMapper());
    final var resolver = new MavenCentralRepository(api);
    final var templateReader = new TemplateReader() {
    };
    final var fileHandler = new FileHandler() {
    };
    final var engine = new MavenEngine();
    return new MavenNewConsoleAppService(templateReader, resolver, fileHandler, engine);
  }

  @Override
  public void createConsoleApp(String artifactId, String groupId, String version) {
    System.err.println("Creating a new Maven project... ⏳");
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

    var pomCreationResult = wrap(() -> createPom(artifactId, groupId, version), "Attempting to create pom.xml... ");
    if (pomCreationResult.isPresent()) {
      System.err.println("Unable to create pom.xml ❌");
      pomCreationResult.get().printStackTrace(System.err);
      return;
    }
    System.err.println("Pom generated successfully ✔️");

    var engineCommandResult = wrap(() -> engine.generateWrapper(workdir), "Attempting to generate maven wrapper... ");
    if (!engineCommandResult.ok()) {
      System.err.println("Unable to generate maven wrapper ❌");
      engineCommandResult.exception().printStackTrace(System.err);
      return;
    }

    System.err.println("Maven wrapper generated successfully ✔️");
  }

  private Optional<Exception> createPom(String artifactId, String groupId, String version) {
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

      final var mainTestClass = templateReader.readTemplate(TEST_CLASS_TEMPLATE)
          .replace("${package}", groupId)
          .replace("${className}", "ConsoleApplicationTest");

      fileHandler.writeToFile(Path.of(testDir.toString(), "ConsoleApplicationTest.java"), mainTestClass);
      return Optional.empty();
    } catch (IOException e) {
      return Optional.of(new RuntimeException(e));
    }
  }
}
