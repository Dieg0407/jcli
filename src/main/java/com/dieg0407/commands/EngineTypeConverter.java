package com.dieg0407.commands;

import picocli.CommandLine.ITypeConverter;

public class EngineTypeConverter implements ITypeConverter<Engine> {

    @Override
    public Engine convert(String value) {
        try {
            return Engine.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid engine type: " + value);
        }
    }
}
