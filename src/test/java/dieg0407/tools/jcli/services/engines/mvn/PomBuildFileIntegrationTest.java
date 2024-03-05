package dieg0407.tools.jcli.services.engines.mvn;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import dieg0407.tools.jcli.FolderDeleter;
import dieg0407.tools.jcli.TestTypes;
import dieg0407.tools.jcli.services.engines.InvalidBuildFileException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
  void shouldThrowInvalidBuildFileExceptionWhenPomFileIsUnparseable() {
    var pomBuildFile = setupPomFile(UNPARSEABLE_POM_FILE);
    assertThatThrownBy(() -> pomBuildFile.dependencyExists("", "", ""))
        .isInstanceOf(InvalidBuildFileException.class)
        .hasMessageContaining("Error parsing pom.xml file");
  }

  @Test
  void shouldReturnFalseWhenPomFileDoesNotHaveDependencies() {
    var pomBuildFile = setupPomFile(POM_WITHOUT_DEPENDENCIES_FILE);
    assertThatThrownBy(() -> pomBuildFile.dependencyExists("", "", ""))
        .isInstanceOf(InvalidBuildFileException.class)
        .hasMessageContaining("No dependencies node under the project. Invalid pom.xml file");
  }

  private PomBuildFile setupPomFile(String pomFile) {
    try (var inputStream = getClass().getClassLoader().getResourceAsStream(pomFile)) {
      if (inputStream == null) {
        throw new RuntimeException("pom.example.xml not found");
      }
      var bytes = inputStream.readAllBytes();
      final var pom = Path.of(TEST_WORKING_DIR.toString(), pomFile);
      if (!pom.toFile().createNewFile()) {
        throw new RuntimeException("pom.xml already exists");
      }
      Files.write(pom, bytes);

      PomFetcher mockedPomFetcher = new PomFetcher() {
        @Override
        public File fetch() {
          return new File(TEST_WORKING_DIR.toFile(), pomFile);
        }
      };
      return new PomBuildFile(mockedPomFetcher, new XmlMapper());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
