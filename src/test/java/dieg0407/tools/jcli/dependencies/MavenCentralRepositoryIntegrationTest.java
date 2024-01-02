package dieg0407.tools.jcli.dependencies;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import dieg0407.tools.jcli.TestTypes;
import dieg0407.tools.jcli.dependencies.api.MavenCentralApiImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@Tag(TestTypes.INTEGRATION)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MavenCentralRepositoryIntegrationTest {

  VersionResolver versionResolver;

  @BeforeAll
  void init() {
    final var api = new MavenCentralApiImpl(new ObjectMapper());
    versionResolver = new MavenCentralRepository(api);
  }

  @Test
  void shouldGetLatestVersion() {
    final var dependency = versionResolver.queryLatestVersion("junit-jupiter-api",
        "org.junit.jupiter");
    assertThat(dependency)
        .isNotEmpty();

    final var dependencyContent = dependency.get();
    assertThat(dependencyContent.artifactId()).isEqualTo("junit-jupiter-api");
    assertThat(dependencyContent.groupId()).isEqualTo("org.junit.jupiter");
    assertThat(dependencyContent.version()).isNotBlank();
  }

  @Test
  void shouldNotGetLatestVersion() {
    final var dependency = versionResolver.queryLatestVersion("some-non-existing-artifact",
        "demos.test.nonexisting");
    assertThat(dependency)
        .isEmpty();
  }
}
