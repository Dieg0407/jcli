package dieg0407.tools.jcli.services.engines.mvn;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import dieg0407.tools.jcli.FolderDeleter;
import dieg0407.tools.jcli.TestTypes;
import dieg0407.tools.jcli.services.engines.InvalidBuildFileException;
import dieg0407.tools.jcli.shared.DependencyType;
import dieg0407.tools.jcli.shared.XmlMapperFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@Tag(TestTypes.INTEGRATION)
@TestInstance(Lifecycle.PER_CLASS)
public class PomBuildFileIntegrationTest {
  private static final Path TEST_WORKING_DIR = Path.of("pom-build-file-it");
  private static final String UNPARSEABLE_POM_FILE = "unparseable-pom.xml";
  private static final String POM_WITHOUT_DEPENDENCIES_FILE = "pom-without-dependencies.xml";
  private static final String VALID_POM_FILE = "valid-pom.xml";

  @BeforeAll
  void beforeAll() throws IOException {
    if (TEST_WORKING_DIR.toFile().exists()) {
      FolderDeleter.deleteFolder(TEST_WORKING_DIR);
    }
    TEST_WORKING_DIR.toFile().mkdirs();
  }
  @AfterAll
  void afterAll() throws Exception {
    FolderDeleter.deleteFolder(TEST_WORKING_DIR);
  }

  @Test
  @DisplayName("Should throw InvalidBuildFileException when pom file is unparseable")
  void shouldThrowInvalidBuildFileExceptionWhenPomFileIsUnparseable() {
    var pomBuildFile = setupPomFile(UNPARSEABLE_POM_FILE, UNPARSEABLE_POM_FILE);
    assertThatThrownBy(() -> pomBuildFile.dependencyExists("", "", ""))
        .isInstanceOf(InvalidBuildFileException.class)
        .hasMessageContaining("Error parsing pom.xml file");
  }

  @Test
  @DisplayName("Should throw InvalidBuildFileException when pom file does not have dependencies")
  void shouldThrowInvalidBuildFileExceptionWhenPomFileDoesNotHaveDependencies() {
    var pomBuildFile = setupPomFile(POM_WITHOUT_DEPENDENCIES_FILE, POM_WITHOUT_DEPENDENCIES_FILE);
    assertThatThrownBy(() -> pomBuildFile.dependencyExists("", "", ""))
        .isInstanceOf(InvalidBuildFileException.class)
        .hasMessageContaining("No dependencies node under the project. Invalid pom.xml file");
  }

  @Test
  @DisplayName("Should add a new dependency when coordinates are not found")
  void shouldAddANewDependencyWhenCoordinatesAreNotFound() throws IOException {
    var pomBuildFile = setupPomFile(VALID_POM_FILE, "pom-01.xml");
    pomBuildFile.addDependency(DependencyType.runtime, "new-artifact", "new-group", "1.0.0");
    assertDependencyExists("new-artifact", "new-group", "1.0.0", "pom-01.xml");
  }

  @Test
  @DisplayName("Should keep the same dependency when it already exists")
  void shouldKeepTheSameDependencyWhenItAlreadyExists() throws IOException {
    var pomBuildFile = setupPomFile(VALID_POM_FILE, "pom-02.xml");
    pomBuildFile.addDependency(DependencyType.test, "junit-jupiter-api", "org.junit.jupiter", "5.9.0");
    assertDependencyExists("junit-jupiter-api", "org.junit.jupiter", "5.9.0", "pom-02.xml");
  }

  @Test
  @DisplayName("Should update version of a dependency when it already exists")
  void shouldUpdateVersionOfADependencyWhenItAlreadyExists() throws IOException {
    var pomBuildFile = setupPomFile(VALID_POM_FILE, "pom-03.xml");
    pomBuildFile.addDependency(DependencyType.test, "junit-jupiter-api", "org.junit.jupiter", "5.10.0");
    assertDependencyExists("junit-jupiter-api", "org.junit.jupiter", "5.10.0", "pom-03.xml");
  }

  private void assertDependencyExists(String artifactId, String groupId, String version, String fileName)
      throws IOException {
    final var xmlMapper = new XmlMapper();
    final var reader = xmlMapper.readTree( new File(TEST_WORKING_DIR.toFile(), fileName));

    assertThat(reader.get("dependencies"))
        .isNotNull();

    final var dependencies = (ArrayNode) reader.get("dependencies").get("dependency");
    for(var dependency : dependencies) {
      if (artifactId.equals(dependency.get("artifactId").asText())
          && groupId.equals(dependency.get("groupId").asText())
          && version.equals(dependency.get("version").asText())) {
        return;
      }
    }
    throw new RuntimeException("Dependency not found");
  }

  private PomBuildFile setupPomFile(String pomFile, String temporalPomFileName) {
    try (var inputStream = getClass().getClassLoader().getResourceAsStream(pomFile)) {
      if (inputStream == null) {
        throw new RuntimeException("pom.example.xml not found");
      }
      var bytes = inputStream.readAllBytes();
      final var pom = Path.of(TEST_WORKING_DIR.toString(), temporalPomFileName);
      if (!pom.toFile().createNewFile()) {
        throw new RuntimeException("pom.xml already exists");
      }
      Files.write(pom, bytes);

      PomHandler mockedPomHandler = new PomHandler() {
        @Override
        public File fetch() {
          return new File(TEST_WORKING_DIR.toFile(), temporalPomFileName);
        }
      };
      return new PomBuildFile(mockedPomHandler, XmlMapperFactory.createInstance());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
