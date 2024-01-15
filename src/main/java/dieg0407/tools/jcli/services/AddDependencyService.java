package dieg0407.tools.jcli.services;

import dieg0407.tools.jcli.shared.DependencyType;

public interface AddDependencyService {
    int add(DependencyType type, String artifactId, String groupId, String version, boolean autoAccept);
}
