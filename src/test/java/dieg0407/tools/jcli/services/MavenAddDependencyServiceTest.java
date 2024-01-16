package dieg0407.tools.jcli.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dieg0407.tools.jcli.TestTypes;
import dieg0407.tools.jcli.services.dependencies.VersionResolver;
import dieg0407.tools.jcli.services.dependencies.models.Dependency;
import dieg0407.tools.jcli.services.engines.BuildFile;
import dieg0407.tools.jcli.shared.DependencyType;
import dieg0407.tools.jcli.shared.ProgramCodes;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(TestTypes.UNIT)
public class MavenAddDependencyServiceTest {

  VersionResolver mockVersionResolver;

  RequireInput mockRequireInput;

  BuildFile mockBuildFile;

  AddDependencyService service;

  @BeforeEach
  void init() {
    mockVersionResolver = mock(VersionResolver.class);
    mockRequireInput = mock(RequireInput.class);
    mockBuildFile = mock(BuildFile.class);
    service = new MavenAddDependencyService(mockVersionResolver, mockRequireInput, mockBuildFile);
  }

  @Test
  void shouldReturnDependencyNotFoundIfNoDependencyCanBeQueried() {
    final var groupId = "groupId";
    final var artifactId = "artifactId";
    final var version = "version";
    final var type = DependencyType.compile;

    when(mockVersionResolver.queryVersions(artifactId, groupId, version)).thenReturn(List.of());

    final var result = service.add(type, artifactId, groupId, version, false);
    verify(mockVersionResolver, times(1)).queryVersions(artifactId, groupId, version);
    verify(mockBuildFile, times(0)).addDependency(type, artifactId, groupId, version);

    assertThat(result).isEqualTo(ProgramCodes.DEPENDENCY_NOT_FOUND);
  }

  @Test
  void shouldReturnSuccessIfOneDependencyIsFoundAndAdded() {
    final var groupId = "groupId";
    final var artifactId = "artifactId";
    final var version = "version";
    final var type = DependencyType.compile;

    when(mockVersionResolver.queryVersions(artifactId, groupId, version))
        .thenReturn(List.of(new Dependency(artifactId, groupId, version)));

    final var result = service.add(type, artifactId, groupId, version, false);
    verify(mockVersionResolver, times(1)).queryVersions(artifactId, groupId, version);
    verify(mockBuildFile, times(1)).addDependency(type, artifactId, groupId, version);

    assertThat(result).isEqualTo(ProgramCodes.SUCCESS);
  }

  @Test
  void shouldReturnSuccessIfMultipleDependenciesAreFoundAndAutoAcceptIsTrue() {
    final var groupId = "groupId";
    final var artifactId = "artifactId";
    final var version = "version";
    final var type = DependencyType.compile;

    final var dep1 = new Dependency(artifactId, groupId, version);
    final var dep2 = new Dependency(artifactId, groupId, "second version");

    when(mockVersionResolver.queryVersions(artifactId, groupId, null))
        .thenReturn(List.of(dep1, dep2));

    final var result = service.add(type, artifactId, groupId, null, true);
    verify(mockVersionResolver, times(1)).queryVersions(artifactId, groupId, null);
    verify(mockBuildFile, times(1)).addDependency(type, artifactId, groupId, version);
    verify(mockRequireInput, times(0)).choose(any());

    assertThat(result).isEqualTo(ProgramCodes.SUCCESS);
  }

  @Test
  void shouldReturnSuccessWithTheUserChosenDependencyWhenMultipleDependenciesAreFoundAndAutoAcceptIsFalse() {
    final var groupId = "groupId";
    final var artifactId = "artifactId";
    final var version = "version";
    final var type = DependencyType.compile;

    final var dep1 = new Dependency(artifactId, groupId, "random version");
    final var dep2 = new Dependency(artifactId, groupId, version);

    when(mockVersionResolver.queryVersions(artifactId, groupId, null))
        .thenReturn(List.of(dep1, dep2));
    when(mockRequireInput.choose(List.of(dep1, dep2))).thenReturn(dep2);

    final var result = service.add(type, artifactId, groupId, null, false);
    verify(mockVersionResolver, times(1)).queryVersions(artifactId, groupId, null);
    verify(mockBuildFile, times(1)).addDependency(type, artifactId, groupId, version);
    verify(mockRequireInput, times(1)).choose(List.of(dep1, dep2));

    assertThat(result).isEqualTo(ProgramCodes.SUCCESS);
  }

  @Test
  void shouldReturnSuccessIfDependencyIsAlreadyInBuildFile() {
    final var groupId = "groupId";
    final var artifactId = "artifactId";
    final var version = "version";
    final var type = DependencyType.compile;

    when(mockBuildFile.dependencyExists(artifactId, groupId, version))
        .thenReturn(true);

    final var result = service.add(type, artifactId, groupId, version, false);
    verify(mockBuildFile, times(0)).addDependency(type, artifactId, groupId, version);
    verify(mockVersionResolver, times(0)).queryVersions(artifactId, groupId, version);

    assertThat(result).isEqualTo(ProgramCodes.SUCCESS);
  }
  
}
