package dieg0407.tools.jcli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.util.concurrent.Callable;

import dieg0407.tools.jcli.models.Engine;

@Command(name = "jcli", mixinStandardHelpOptions = true, version = "1.0.0")
public class JcliApplication implements Callable<Integer> {

  @Option(names = { "-v",
      "--verbose" }, defaultValue = "false", description = "Verbose mode. Helpful for troubleshooting.")
  boolean verbose = false;

  @Option(names = { "-e",
      "--engine" }, defaultValue = "MAVEN", description = "The build engine to use. Valid values are: ${COMPLETION-CANDIDATES}. Default: ${DEFAULT-VALUE}.")
  Engine engine = Engine.MAVEN;

  @Override
  public Integer call() throws Exception {
    System.out.println(verbose);
    System.out.println(engine);
    return 0;
  }

  public static void main(String[] args) {
    int exitCode = new CommandLine(new JcliApplication()).execute(args);
    System.exit(exitCode);
  }
}
