package com.dieg0407.commands;

import picocli.CommandLine.ITypeConverter;

public class ProjectTypeConverter implements ITypeConverter<ProjectType> {

    @Override
    public ProjectType convert(String value) {
        try {
            return ProjectType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid project type: " + value);
        }
    }

}
