package dieg0407.tools.jcli.services.engines;

public class InvalidBuildFileException extends RuntimeException {

  public InvalidBuildFileException(String message) {
    super(message);
  }

  public InvalidBuildFileException(String message, Throwable cause) {
    super(message, cause);
  }
}
