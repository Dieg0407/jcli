package dieg0407.tools.jcli.commands;

import dieg0407.tools.jcli.JcliApplication;
import dieg0407.tools.jcli.commands.models.Template;
import dieg0407.tools.jcli.commands.validators.GroupIdValidator;
import dieg0407.tools.jcli.services.MavenNewConsoleAppService;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

@Command(name = "new", mixinStandardHelpOptions = true, version = "1.0.0")
public class NewCommand implements Callable<Integer> {

  @ParentCommand
  JcliApplication parent;

  @Parameters(paramLabel = "TEMPLATE", description = "The template to use. This is a required field. Valid values are: ${COMPLETION-CANDIDATES}.")
  Template template;

  @Option(names = {"-a",
      "--artifactId"}, required = true, description = "The artifactId of the project.")
  String artifactId;

  @Option(names = {"-g", "--groupId"}, required = true, description = "The groupId of the project.")
  String groupId;

  @Option(names = {"-V",
      "--version"}, defaultValue = "1.0-SNAPSHOT", description = "The version of the project.")
  String version;

  @Override
  public Integer call() throws Exception {
    if (groupId == null || !GroupIdValidator.isValid(groupId)) {
      System.out.println("Invalid groupId.");
      return ProgramCodes.INVALID_GROUP_ID_ERROR_CODE;
    }
    if (artifactId == null || artifactId.isBlank()) {
      System.out.println("Invalid artifactId.");
      return ProgramCodes.INVALID_ARTIFACT_ID_ERROR_CODE;
    }
    if (version == null || version.isBlank()) {
      System.out.println("Invalid version.");
      return ProgramCodes.INVALID_VERSION_ERROR_CODE;
    }

    final var service = MavenNewConsoleAppService.getInstance();
    return service.createConsoleApp(artifactId, groupId, version);
  }

}
