package dieg0407.tools.jcli.services;

import static org.assertj.core.api.Assertions.assertThat;

import dieg0407.tools.jcli.FolderDeleter;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class MavenNewProjectServiceIntegrationTest {

  NewProjectService newProjectService;

  static final String PROJECT_NAME = "test-project";

  @BeforeAll
  void beforeAll() {
    newProjectService = MavenNewProjectService.getInstance();
  }

  @AfterAll
  void afterAll() throws Exception {
    FolderDeleter.deleteFolder(Path.of(PROJECT_NAME));
  }

  @Test
  void shouldCreateConsoleAppArchetype() {
    newProjectService.createConsoleApp(PROJECT_NAME, "com.demo", "1.0.0-SNAPSHOT");

    // validate existence of src/main/java/com/demo/ConsoleApplication.java
    final var file = Path.of(PROJECT_NAME, "src", "main", "java", "com", "demo",
        "ConsoleApplication.java");
    assertThat(file).exists();

    // validate existence of pom.xml
    final var pom = Path.of(PROJECT_NAME, "pom.xml");
    assertThat(pom).exists();
  }
}
