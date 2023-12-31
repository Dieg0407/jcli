package dieg0407.tools.jcli.engines;

import java.io.File;

/***
 * Controls access to the Maven command line
 */
public interface Engine {

  CommandResult generateWrapper(File directory);
}
