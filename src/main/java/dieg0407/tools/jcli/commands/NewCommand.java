package dieg0407.tools.jcli.commands;

import dieg0407.tools.jcli.JcliApplication;
import dieg0407.tools.jcli.models.Template;
import dieg0407.tools.jcli.services.MavenNewProjectService;
import dieg0407.tools.jcli.validators.GroupIdValidator;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;
import java.util.concurrent.Callable;

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
    final var service = MavenNewProjectService.getInstance();
    if (!GroupIdValidator.isValid(groupId)) {
      System.out.println("Invalid groupId.");
      return 1;
    }
    service.createConsoleApp(artifactId, groupId, version);
    return 0;
  }

}
