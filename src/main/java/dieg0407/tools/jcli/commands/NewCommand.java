package dieg0407.tools.jcli.commands;

import dieg0407.tools.jcli.JcliApplication;
import dieg0407.tools.jcli.models.Template;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;
import java.util.concurrent.Callable; // Import the Callable class

@Command(name = "new", mixinStandardHelpOptions = true, version = "1.0.0")
public class NewCommand implements Callable<Integer> {
  @ParentCommand
  JcliApplication parent;

  @Parameters(paramLabel = "TEMPLATE", description = "The template to use. This is a required field. Valid values are: ${COMPLETION-CANDIDATES}.")
  Template template;

  @Option(names = { "-a", "--artifactId" }, required = true, description = "The artifactId of the project.")
  String artifactId;

  @Option(names = { "-g", "--groupId" }, required = true, description = "The groupId of the project.")
  String groupId;

  @Option(names = { "-V", "--version" }, defaultValue = "1.0-SNAPSHOT", description = "The version of the project.")
  String version;

  @Override
  public Integer call() throws Exception {
    System.err.println(parent.getEngine());
    System.err.println(template);
    System.err.println(artifactId);
    System.err.println(groupId);
    System.err.println(version);
    return 0;
  }

}
