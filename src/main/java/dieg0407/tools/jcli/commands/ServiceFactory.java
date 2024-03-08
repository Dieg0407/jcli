package dieg0407.tools.jcli.commands;

import dieg0407.tools.jcli.services.MavenNewConsoleAppService;
import dieg0407.tools.jcli.services.NewConsoleAppService;

public interface ServiceFactory {

  static ServiceFactory getInstance() {
    return new ServiceFactory() {
    };
  }

  default NewConsoleAppService newConsoleAppService() {
    return MavenNewConsoleAppService.getInstance();
  }
}
