package dieg0407.tools.jcli.services;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dieg0407.tools.jcli.directory.FileHandler;
import dieg0407.tools.jcli.directory.FileHandler.Result;
import dieg0407.tools.jcli.models.Dependency;
import dieg0407.tools.jcli.services.dependencies.VersionResolver;
import dieg0407.tools.jcli.services.templates.TemplateReader;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class MavenNewProjectServiceTest {

  MavenNewProjectService projectService;

  TemplateReader templateReader;

  VersionResolver versionResolver;

  FileHandler fileHandler;

  @BeforeEach
  void init() {
    templateReader = Mockito.mock(TemplateReader.class);
    versionResolver = Mockito.mock(VersionResolver.class);
    fileHandler = Mockito.mock(FileHandler.class);
    projectService = new MavenNewProjectService(templateReader, versionResolver, fileHandler);
  }

  @Test
  void shouldThrowRuntimeExceptionWhenJunitVersionCouldNotBeQueried() {
    final var artifact = "artifact";
    final var groupId = "test.demo";
    Mockito.when(versionResolver.queryLatestVersion(artifact, groupId))
        .thenReturn(Optional.empty());
    Mockito.when(fileHandler.createFolder(Path.of("artifact").toFile())).thenReturn(Result.CREATED);

    assertThatThrownBy(() ->
        projectService.createConsoleApp(artifact, groupId, "1.0.0-SNAPSHOT")
    ).isInstanceOf(RuntimeException.class)
        .withFailMessage("Unable to fetch junit5 latest version");
  }

  @Test
  void shouldNotCallVersionResolverIfWorkdirFailedToCreate() {
    final var artifact = "artifact";
    final var groupId = "test.demo";
    Mockito.when(fileHandler.createFolder(Path.of("artifact").toFile())).thenReturn(Result.ERROR);

    projectService.createConsoleApp(artifact, groupId, "1.0.0-SNAPSHOT");

    Mockito.verify(versionResolver, Mockito.never())
        .queryLatestVersion(Mockito.anyString(), Mockito.anyString());
  }

  @Test
  void shouldNotCallVersionResolverIfWorkdirAlreadyExists() {
    final var artifact = "artifact";
    final var groupId = "test.demo";
    Mockito.when(fileHandler.createFolder(Path.of("artifact").toFile()))
        .thenReturn(Result.ALREADY_EXISTS);

    projectService.createConsoleApp(artifact, groupId, "1.0.0-SNAPSHOT");

    Mockito.verify(versionResolver, Mockito.never())
        .queryLatestVersion(Mockito.anyString(), Mockito.anyString());
  }

  @Test
  void shouldReplaceTemplatePartsByTheCorrectValues() throws Exception {
    final var artifact = "artifact";
    final var groupId = "test.demo";
    final var version = "1.0.0-SNAPSHOT";
    final var junit5Version = "5.7.2";
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

    Mockito.when(fileHandler.createFolder(Path.of("artifact").toFile()))
        .thenReturn(Result.CREATED);
    Mockito.when(templateReader.readTemplate(
            format("/%s/%s", "maven-templates", "console-app.pom.template")))
        .thenReturn(pomTemplate);
    Mockito.when(templateReader.readTemplate("/generic-templates/main.template"))
        .thenReturn(classTemplate);
    Mockito.when(versionResolver.queryLatestVersion("junit-jupiter", "org.junit.jupiter"))
        .thenReturn(
            Optional.of(new Dependency("junit-jupiter", "org.junit.jupiter", junit5Version)));

    projectService.createConsoleApp(artifact, groupId, version);

    Mockito.verify(fileHandler).writeToFile(Path.of(artifact, "pom.xml"), finalPom);
    Mockito.verify(fileHandler).writeToFile(Path.of(artifact, "src", "main", "java", "test", "demo", "ConsoleApplication.java"), finalClass);
    Mockito.verify(fileHandler).createFolder(Path.of(artifact, "src", "main", "java", "test", "demo").toFile());
    Mockito.verify(fileHandler).createFolder(Path.of(artifact, "src", "main", "resources").toFile());
    Mockito.verify(fileHandler).createFolder(Path.of(artifact, "src", "test", "java", "test", "demo").toFile());
    Mockito.verify(fileHandler).createFolder(Path.of(artifact, "src", "test", "resources").toFile());
  }
}
