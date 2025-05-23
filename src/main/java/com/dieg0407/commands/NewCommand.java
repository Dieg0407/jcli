package com.dieg0407.commands;

import java.util.Optional;
import java.util.concurrent.Callable;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "new", description = "Creates a new project.")
public class NewCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "The type of project to create.")
    private ProjectType projectType;

    @Option(names = { "-n", "--name" }, description = "The name of the project.")
    private Optional<String> projectName;

    @Option(names = { "-g", "--groupId" }, description = "The group ID of the project.")
    private Optional<String> groupId;

    @Option(names = { "-a", "--artifactId" }, description = "The artifact ID of the project.")
    private Optional<String> artifactId;

    @Option(names = { "-v", "--version" }, description = "The version of the project.")
    private Optional<String> version;

    @Override
    public Integer call() throws Exception {
        // just print the data
        System.out.println("Project Type: " + projectType);
        System.out.println("Project Name: " + projectName);
        System.out.println("Group ID: " + groupId);
        System.out.println("Artifact ID: " + artifactId);
        System.out.println("Version: " + version);

        return 0;
    }

    // internal types
    public static enum ProjectType {
        CONSOLE
    }
}
