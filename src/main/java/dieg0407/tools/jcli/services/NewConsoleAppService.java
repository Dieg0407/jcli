package dieg0407.tools.jcli.services;

public interface NewConsoleAppService {

  String MAIN_CLASS_TEMPLATE = "/generic-templates/main.template";
  String CONSOLE_APP_TEMPLATE_NAME = "console-app.pom.template";

  void createConsoleApp(String artifactId, String groupId, String version);
}
