package dieg0407.tools.jcli.services.dependencies.mvn;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import dieg0407.tools.jcli.TestTypes;
import dieg0407.tools.jcli.services.dependencies.VersionResolver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@Tag(TestTypes.INTEGRATION)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MavenCentralVersionResolverIntegrationTest {

  VersionResolver versionResolver;

  @BeforeAll
  void init() {
    versionResolver = new MavenCentralVersionResolver(new ObjectMapper());
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

  @Test
  void shouldFetchAllVersions() {
    // query many artifacts for a generic id
    final var dependencies = versionResolver.query("test", null, null);
    assertThat(dependencies)
        .isNotEmpty()
        .hasSizeGreaterThan(1);

    final var dependency = dependencies.get(0);
    final var chosenDependency = versionResolver.query(dependency.artifactId(), dependency.groupId(), dependency.version());
    assertThat(chosenDependency)
        .isNotEmpty()
        .hasSize(1)
        .isEqualTo(singletonList(dependency));
  }
}
