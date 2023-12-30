package dieg0407.tools.jcli.directory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface FileHandler {

  default Result createFolder(File folder)  {
    if (folder.exists()) {
      return Result.ALREADY_EXISTS;
    }
    if (!folder.mkdirs()) {
      return Result.ERROR;
    }
    return Result.CREATED;
  }

  default void writeToFile(Path path, CharSequence charSequence) throws IOException {
    Files.writeString(path, charSequence);
  }

  enum Result {
    CREATED,
    ALREADY_EXISTS,
    ERROR
  }
}
