package dieg0407.tools.jcli.services;

public interface JavaVersion {

  default int get() {
    String version = System.getProperty("java.version");
    if (version.startsWith("1.")) {
      throw new RuntimeException("version 1.x is not supported");
    } else {
      final int dot = version.indexOf(".");
      if (dot != -1) {
        version = version.substring(0, dot);
      }
      return Integer.parseInt(version);
    }
  }
}
