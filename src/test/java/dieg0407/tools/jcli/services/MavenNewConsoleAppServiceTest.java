package dieg0407.tools.jcli.services;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dieg0407.tools.jcli.TestTypes;
import dieg0407.tools.jcli.shared.ProgramCodes;
import dieg0407.tools.jcli.services.FileHandler.Result;
import dieg0407.tools.jcli.services.dependencies.VersionResolver;
import dieg0407.tools.jcli.services.dependencies.models.Dependency;
import dieg0407.tools.jcli.services.engines.CommandResult;
import dieg0407.tools.jcli.services.engines.Engine;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@Tag(TestTypes.UNIT)
public class MavenNewConsoleAppServiceTest {

  MavenNewConsoleAppService projectService;

  TemplateReader templateReader;

  VersionResolver versionResolver;

  FileHandler fileHandler;

  Engine engine;

  JavaVersion javaVersion;

  @BeforeEach
  void init() {
    templateReader = mock(TemplateReader.class);
    versionResolver = mock(VersionResolver.class);
    fileHandler = mock(FileHandler.class);
    engine = mock(Engine.class);
    javaVersion = mock(JavaVersion.class);
    projectService = new MavenNewConsoleAppService(templateReader, versionResolver, fileHandler,
        engine, javaVersion);
  }

  @Test
  void shouldReturnUnableToCreatePomWhenJunitVersionCouldNotBeQueried() {
    final var artifact = "artifact";
    final var groupId = "test.demo";
    when(versionResolver.queryLatestVersion(artifact, groupId))
        .thenReturn(Optional.empty());
    when(fileHandler.createFolder(Path.of("artifact").toFile())).thenReturn(Result.CREATED);

    final var code = projectService.createConsoleApp(artifact, groupId, "1.0.0-SNAPSHOT");
    assertThat(code).isEqualTo(ProgramCodes.UNABLE_TO_CREATE_POM);
  }

  @Test
  void shouldNotCallVersionResolverIfWorkdirFailedToCreate() {
    final var artifact = "artifact";
    final var groupId = "test.demo";
    when(fileHandler.createFolder(Path.of("artifact").toFile())).thenReturn(Result.ERROR);

    final var code = projectService.createConsoleApp(artifact, groupId, "1.0.0-SNAPSHOT");
    assertThat(code).isEqualTo(ProgramCodes.UNABLE_TO_CREATE_DIRECTORY);

    verify(versionResolver, Mockito.never())
        .queryLatestVersion(Mockito.anyString(), Mockito.anyString());
  }

  @Test
  void shouldNotCallVersionResolverIfWorkdirAlreadyExists() {
    final var artifact = "artifact";
    final var groupId = "test.demo";
    when(fileHandler.createFolder(Path.of("artifact").toFile()))
        .thenReturn(Result.ALREADY_EXISTS);

    final var code = projectService.createConsoleApp(artifact, groupId, "1.0.0-SNAPSHOT");
    assertThat(code).isEqualTo(ProgramCodes.DIRECTORY_ALREADY_EXISTS);

    verify(versionResolver, Mockito.never())
        .queryLatestVersion(Mockito.anyString(), Mockito.anyString());
  }

  @Test
  void shouldReplaceTemplatePartsByTheCorrectValues() throws Exception {
    final var artifact = "artifact";
    final var groupId = "test.demo";
    final var version = "1.0.0-SNAPSHOT";
    final var junit5Version = "5.7.2";
    final var javaVersionNumber = 17;
    final var pomTemplate = """
        ${artifactId}
        ${groupId}
        ${version}
        ${junit5Version}
        ${javaVersion}
        """;
    final var finalPom = """
        artifact
        test.demo
        1.0.0-SNAPSHOT
        5.7.2
        17
        """;
    final var classTemplate = """
        ${package}
        ${className}
        """;
    final var finalClass = """
        test.demo
        ConsoleApplication
        """;

    final var testClassTemplate = """
        ${package}
        ${className}
        """;
    final var finalTestClass = """
        test.demo
        ConsoleApplicationTest
        """;

    when(fileHandler.createFolder(Path.of("artifact").toFile()))
        .thenReturn(Result.CREATED);
    when(templateReader.readTemplate(
        format("/%s/%s", "maven-templates", "console-app.pom.template")))
        .thenReturn(pomTemplate);
    when(templateReader.readTemplate("/generic-templates/main.template"))
        .thenReturn(classTemplate);
    when(templateReader.readTemplate("/generic-templates/main-test.template"))
        .thenReturn(testClassTemplate);
    when(versionResolver.queryLatestVersion("junit-jupiter", "org.junit.jupiter"))
        .thenReturn(
            Optional.of(new Dependency("junit-jupiter", "org.junit.jupiter", junit5Version)));
    when(javaVersion.get()).thenReturn(17);

    final var workdir = Path.of(artifact);
    when(engine.generateWrapper(workdir.toFile()))
        .thenReturn(new CommandResult(CommandResult.SUCCESS, Optional.empty(), Optional.empty()));
    projectService.createConsoleApp(artifact, groupId, version);

    verify(fileHandler).writeToFile(Path.of(artifact, "pom.xml"), finalPom);
    verify(fileHandler).writeToFile(
        Path.of(artifact, "src", "main", "java", "test", "demo", "ConsoleApplication.java"),
        finalClass);
    verify(fileHandler).writeToFile(
        Path.of(artifact, "src", "test", "java", "test", "demo", "ConsoleApplicationTest.java"),
        finalTestClass);
    verify(fileHandler)
        .createFolder(Path.of(artifact, "src", "main", "java", "test", "demo").toFile());
    verify(fileHandler)
        .createFolder(Path.of(artifact, "src", "main", "resources").toFile());
    verify(fileHandler).createFolder(
        Path.of(artifact, "src", "test", "java", "test", "demo").toFile());
    verify(fileHandler)
        .createFolder(Path.of(artifact, "src", "test", "resources").toFile());
    verify(engine).generateWrapper(workdir.toFile());
    verify(javaVersion).get();
  }
}
