use std::io::{self, Write};

use clap::{Args, Subcommand};
use regex::Regex;

#[derive(Debug, Args)]
pub struct NewCommand {
    #[clap(subcommand)]
    pub subcommand: NewSubcommands,
}

#[derive(Debug, Subcommand)]
pub enum NewSubcommands {
    /// Create a new console application
    Console(ConsoleArgs),
}

#[derive(Debug, Args)]
pub struct ConsoleArgs {
    /// The group ID for the Java package
    #[clap(long)]
    pub group_id: Option<String>,

    /// The version of the application
    #[clap(long)]
    pub version: Option<String>,

    /// The Java target version
    #[clap(long)]
    pub java_target: Option<String>,

    /// The build engine to use
    #[clap(long, value_parser = ["maven", "gradle"])]
    pub engine: Option<String>,

    /// The project name
    #[clap(long)]
    pub name: Option<String>,
}

impl NewCommand {
    pub fn execute(&self) -> Result<(), String> {
        match &self.subcommand {
            NewSubcommands::Console(args) => create_console_app(args),
        }
    }
}

fn create_console_app(args: &ConsoleArgs) -> Result<(), String> {
    let name = args.name.as_ref().map_or_else(
        || {
            print!("Enter the name of the application: ");
            io::stdout().flush().expect("Failed to flush stdout");

            let mut input = String::new();
            std::io::stdin()
                .read_line(&mut input)
                .expect("Failed to read line");
            input.trim().to_string()
        },
        |n| n.to_string(),
    );

    // validate name format
    if !name.chars().all(|c| c.is_alphanumeric() || c == '_') {
        return Err(
            "Invalid application name. Only alphanumeric characters and underscores are allowed."
                .to_string(),
        );
    }

    let group_id = args.group_id.as_ref().map_or_else(
        || {
            print!("Enter the group ID for the Java package: ");
            io::stdout().flush().expect("Failed to flush stdout");

            let mut input = String::new();
            std::io::stdin()
                .read_line(&mut input)
                .expect("Failed to read line");
            input.trim().to_string()
        },
        |id| id.to_string(),
    );

    // validate group ID format
    let group_id_regex = Regex::new(r"^[a-zA-Z][a-zA-Z0-9]*(\.[a-zA-Z][a-zA-Z0-9]*)*$")
        .expect("Failed to compile regex for group ID validation");
    if !group_id_regex.is_match(&group_id) {
        return Err("Invalid group ID format. It should be a valid Java package name.".to_string());
    }

    let version = args.version.as_ref().map_or_else(
        || {
            print!("Enter the version of the application (default: 1.0.0): ");
            io::stdout().flush().expect("Failed to flush stdout");

            let mut input = String::new();
            std::io::stdin()
                .read_line(&mut input)
                .expect("Failed to read line");
            let input = input.trim().to_string();

            if input == "" {
                "1.0.0".to_string() // default version
            } else {
                input
            }
        },
        |v| v.to_string(),
    );

    // validate version format
    let version_regex =
        Regex::new(r"^\d+\.\d+\.\d+$").expect("Failed to compile regex for version validation");
    if !version_regex.is_match(&version) {
        return Err(
            "Invalid version format. It should be in the format X.Y.Z (e.g., 1.0.0).".to_string(),
        );
    }

    let java_target = args.java_target.as_ref().map_or_else(
        || {
            print!("Enter the Java target version (default: 21): ");
            io::stdout().flush().expect("Failed to flush stdout");

            let mut input = String::new();
            std::io::stdin()
                .read_line(&mut input)
                .expect("Failed to read line");

            let input = input.trim().to_string();
            if input == "" {
                "21".to_string() // default Java target version
            } else {
                input
            }
        },
        |target| target.to_string(),
    );

    // check if is valid java target version
    // TODO: add non LTS versions
    if !["1.8", "11", "17", "21"].contains(&java_target.as_str()) {
        return Err(
            "Invalid Java target version. Supported versions are 1.8, 11, 17, and 21.".to_string(),
        );
    }

    let engine = args.engine.as_ref().map_or_else(
        || {
            print!("Enter the build engine (maven or gradle): ");
            io::stdout().flush().expect("Failed to flush stdout");

            let mut input = String::new();
            std::io::stdin()
                .read_line(&mut input)
                .expect("Failed to read line");
            let input = input.trim().to_string();
            if input == "" {
                "maven".to_string() // default build engine
            } else {
                input
            }
        },
        |e| e.to_string(),
    );

    // validate build engine
    if !["maven", "gradle"].contains(&engine.as_str()) {
        return Err("Invalid build engine. Supported engines are maven and gradle.".to_string());
    }

    println!("Creating console application with the following details:");
    println!("Name: {}", name);
    println!("Group ID: {}", group_id);
    println!("Version: {}", version);
    println!("Java Target: {}", java_target);
    println!("Build Engine: {}", engine);

    Ok(())
}
