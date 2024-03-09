package dieg0407.tools.jcli.services.engines.mvn;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public interface PomHandler {
  static final String POM_FILE_NAME = "pom.xml";

  default File fetch() {
    return new File(POM_FILE_NAME);
  }

  default void save(String content) {
    final var file = fetch();
    try {
      Files.writeString(file.toPath(), content);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
