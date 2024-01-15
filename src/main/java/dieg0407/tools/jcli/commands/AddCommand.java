package dieg0407.tools.jcli.commands;

import dieg0407.tools.jcli.JcliApplication;
import dieg0407.tools.jcli.shared.DependencyType;
import dieg0407.tools.jcli.commands.models.ModifiableObject;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "add", mixinStandardHelpOptions = true, version = "1.0.0")
public class AddCommand implements Callable<Integer> {
    @CommandLine.ParentCommand
    JcliApplication parent;

    @CommandLine.Parameters(paramLabel = "object", description = "The object to add. Valid values are: ${COMPLETION-CANDIDATES}.", defaultValue = "dependency")
    ModifiableObject modifiableObject;

    @CommandLine.Option(names = {"-t", "--type"}, description = "The type of the dependency. Valid values are: ${COMPLETION-CANDIDATES}.", defaultValue = "compile")
    DependencyType type;

    @CommandLine.Option(names = {"-a", "--artifactId"}, description = "The artifactId of the dependency.", required = true)
    String artifactId;

    @CommandLine.Option(names = {"-g", "--groupId"}, description = "The groupId of the dependency.")
    String groupId;

    @CommandLine.Option(names = {"-V", "--version"}, description = "The version of the dependency.")
    String version;

    @CommandLine.Option(names = {"-A", "--auto-accept"}, description = "Automatically accept the first option that matches the provided values if more than one is found.")
    boolean autoAccept;

    @Override
    public Integer call() throws Exception {
        return 0;
    }
}
