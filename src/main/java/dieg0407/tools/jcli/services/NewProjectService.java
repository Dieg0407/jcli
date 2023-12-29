package dieg0407.tools.jcli.services;

public interface NewProjectService {
  String GENERICS_TEMPLATES = "generic-templates";
  String CONSOLE_APP_TEMPLATE_NAME = "console-app.template";

  void createConsoleApp(String artifactId, String groupId, String version);
}
