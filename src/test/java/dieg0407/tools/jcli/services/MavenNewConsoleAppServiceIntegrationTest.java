package dieg0407.tools.jcli.services;

import static org.assertj.core.api.Assertions.assertThat;

import dieg0407.tools.jcli.FolderDeleter;
import dieg0407.tools.jcli.TestTypes;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@Tag(TestTypes.INTEGRATION)
@TestInstance(Lifecycle.PER_CLASS)
public class MavenNewConsoleAppServiceIntegrationTest {

  NewConsoleAppService newConsoleAppService;

  static final String PROJECT_NAME = "test-project";
  static final Path PROJECT_PATH = Path.of(PROJECT_NAME);

  @BeforeAll
  void beforeAll() throws IOException {
    if (PROJECT_PATH.toFile().exists()) {
      FolderDeleter.deleteFolder(PROJECT_PATH);
    }
    newConsoleAppService = MavenNewConsoleAppService.getInstance();
  }

  @AfterAll
  void afterAll() throws Exception {
    FolderDeleter.deleteFolder(PROJECT_PATH);
  }

  @Test
  void shouldCreateConsoleAppArchetype() {
    newConsoleAppService.createConsoleApp(PROJECT_NAME, "com.demo", "1.0.0-SNAPSHOT");

    // validate existence of src/main/java/com/demo/ConsoleApplication.java
    final var file = Path.of(PROJECT_NAME, "src", "main", "java", "com", "demo",
        "ConsoleApplication.java");
    assertThat(file).exists();

    // validate existence of src/test/java/com/demo/ConsoleApplicationTest.java
    final var testFile = Path.of(PROJECT_NAME, "src", "test", "java", "com", "demo",
        "ConsoleApplicationTest.java");
    assertThat(testFile).exists();

    // validate existence of pom.xml
    final var pom = Path.of(PROJECT_NAME, "pom.xml");
    assertThat(pom).exists();
    // validate existence of wrapper folder and files
    final var wrapper = Path.of(PROJECT_NAME, "mvnw");
    assertThat(wrapper).exists();
    final var wrapperWindows = Path.of(PROJECT_NAME, "mvnw.cmd");
    assertThat(wrapperWindows).exists();
    final var wrapperFolder = Path.of(PROJECT_NAME, ".mvn");
    assertThat(wrapperFolder).exists();
  }
}
