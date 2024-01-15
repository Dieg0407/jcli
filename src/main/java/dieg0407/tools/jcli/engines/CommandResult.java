package dieg0407.tools.jcli.engines;

import java.util.Optional;

public record CommandResult(int exitCode, Optional<String> err, Optional<Exception> exception) {

  public static int SUCCESS = 0;
}
