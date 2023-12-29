package dieg0407.tools.jcli.services;

public interface NewProjectService {

  String MAIN_CLASS_TEMPLATE = "/generic-templates/main.template";
  String CONSOLE_APP_TEMPLATE_NAME = "console-app.template";

  void createConsoleApp(String artifactId, String groupId, String version);
}
