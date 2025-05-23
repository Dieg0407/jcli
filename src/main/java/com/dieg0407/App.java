package com.dieg0407;

import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "jcli", mixinStandardHelpOptions = true, version = "1.0.0", description = "A simple Java CLI that emulates net core nuget.")
public class App implements Callable<Integer> {
    public static void main(String[] args) {
        final int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("Hello World!");
        return 0;
    }

}
