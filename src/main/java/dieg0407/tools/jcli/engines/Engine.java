package dieg0407.tools.jcli.engines;

import java.io.IOException;

/***
 * Controls access to the Maven command line
 */
public interface Engine {

  default CommandResult generateWrapper() {
    try {
      final var processBuilder = new ProcessBuilder("mvn", "wrapper:wrapper");
      final var process = processBuilder.start();
      final var exitCode = process.waitFor();
      return new CommandResult(exitCode == 0, null);
    } catch (IOException | InterruptedException e) {
      return new CommandResult(false, e);
    }
  }
}
