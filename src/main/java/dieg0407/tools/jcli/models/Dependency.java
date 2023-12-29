package dieg0407.tools.jcli.models;

public record Dependency(String artifactId, String groupId, String version) implements Comparable<Dependency>{

  @Override
  public int compareTo(Dependency o) {
    return 0;
  }
}
