package dieg0407.tools.jcli.engines;

import dieg0407.tools.jcli.engines.models.Os;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class MavenEngine implements Engine {

  @Override
  public CommandResult generateWrapper(File directory) {
    try {
      final var processBuilder = createProcessBuilder(directory, "mvn", "wrapper:wrapper");
      processBuilder.directory(directory);
      final var process = processBuilder.start();
      final var exitCode = process.waitFor();
      if (exitCode == 0) {
        return new CommandResult(exitCode, Optional.empty(), Optional.empty());
      }
      final var errorMessage = new String(process.getErrorStream().readAllBytes());
      return new CommandResult(exitCode, Optional.of(errorMessage), Optional.empty());
    } catch (IOException | InterruptedException e) {
      return new CommandResult(-1, Optional.empty(), Optional.of(e));
    }
  }

  private ProcessBuilder createProcessBuilder(File directory, String... command) {
    ProcessBuilder processBuilder;
    final var os = detectOs();
    if (os == Os.WINDOWS) {
      processBuilder = new ProcessBuilder("cmd.exe", "/c", String.join(" ", command));
    } else {
      processBuilder = new ProcessBuilder("sh", "-c", String.join(" ", command));
    }
    processBuilder.directory(directory);
    return processBuilder;
  }

  private Os detectOs() {
    String osName = System.getProperty("os.name").toLowerCase();
    if (osName.contains("win")) {
      return Os.WINDOWS;
    } else if (osName.contains("mac")) {
      return Os.MAC;
    } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
      return Os.LINUX;
    } else {
      return Os.UNKNOWN;
    }
  }
}
