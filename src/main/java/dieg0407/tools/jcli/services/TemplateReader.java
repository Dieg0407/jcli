package dieg0407.tools.jcli.services;

import java.io.IOException;

public interface TemplateReader {

  default String readTemplate(String templatePath) {
    try (final var stream = getClass().getResourceAsStream(templatePath)) {
      if (stream == null) {
        throw new RuntimeException("Template " + templatePath + " not found");
      }
      return new String(stream.readAllBytes());
    } catch (IOException e) {
      throw new RuntimeException("Error reading template " + templatePath, e);
    }
  }
}
