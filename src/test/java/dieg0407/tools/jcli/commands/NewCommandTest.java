package dieg0407.tools.jcli.commands;

import static org.assertj.core.api.Assertions.assertThat;

import dieg0407.tools.jcli.commands.models.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

public class NewCommandTest {

  NewCommand newCommand;

  @BeforeEach
  void init() {
    newCommand = new NewCommand();
    newCommand.groupId = "com.example";
    newCommand.artifactId = "example";
    newCommand.version = "1.0.0";
    newCommand.template = Template.console_app;
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"int.example", "1234.example.org", ""})
  void validateGroupId(String invalidGroupIds) throws Exception {
    newCommand.groupId = invalidGroupIds;
    final var exitCode = newCommand.call();
    assertThat(exitCode).isEqualTo(ProgramCodes.INVALID_GROUP_ID_ERROR_CODE);
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {""})
  void validateArtifactId(String invalidArtifactId) throws Exception {
    newCommand.artifactId = invalidArtifactId;
    final var exitCode = newCommand.call();
    assertThat(exitCode).isEqualTo(ProgramCodes.INVALID_ARTIFACT_ID_ERROR_CODE);
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {""})
  void validateVersion(String invalidVersion) throws Exception {
    newCommand.version = invalidVersion;
    final var exitCode = newCommand.call();
    assertThat(exitCode).isEqualTo(ProgramCodes.INVALID_VERSION_ERROR_CODE);
  }
}
