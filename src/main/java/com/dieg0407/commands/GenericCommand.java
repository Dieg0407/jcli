package com.dieg0407.commands;

import picocli.CommandLine.Option;

public abstract class GenericCommand {

    @Option(names = {
            "-e, --engine" }, description = "The engine to use.", defaultValue = "maven", converter = EngineTypeConverter.class)
    public Engine engine;

    @Option(names = { "-vb, --verbose" }, description = "Enable verbose output.", defaultValue = "false")
    public boolean verbose;
}
