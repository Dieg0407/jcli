package dieg0407.tools.jcli.services;

public interface ServiceFactory {

  static ServiceFactory getInstance() {
    return new ServiceFactory() {
    };
  }

  default NewConsoleAppService newConsoleAppService() {
    return MavenNewConsoleAppService.getInstance();
  }
}
