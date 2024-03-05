package dieg0407.tools.jcli.services.engines.mvn;

import java.io.File;

public interface PomFetcher {

  default File fetch() {
    return new File("pom.xml");
  }

}
