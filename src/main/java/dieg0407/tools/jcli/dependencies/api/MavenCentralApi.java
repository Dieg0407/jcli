package dieg0407.tools.jcli.dependencies.api;

import java.util.List;

public interface MavenCentralApi {

  List<MavenCentralDependency> query(String artifactId, String groupId);
}
