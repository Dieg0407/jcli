mod arguments;
mod templates;

use arguments::{JavaProjectArgumentsProviders, default_java_arguments_providers};
use clap::{Args, Subcommand};
use regex::Regex;
use templates::create_maven_console_app;

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
            NewSubcommands::Console(args) => {
                create_console_app(args, default_java_arguments_providers())
            }
        }
    }
}

fn create_console_app(
    args: &ConsoleArgs,
    arguments_provider: JavaProjectArgumentsProviders,
) -> Result<(), String> {
    // Project name input with validation
    let name = match &args.name {
        Some(name) => name.to_string(),
        None => arguments_provider
            .name
            .get_arguments()
            .map_err(|e| e.to_string())?,
    };

    // Check if the project name is valid
    if name.is_empty()
        || !name
            .chars()
            .all(|c| c.is_alphanumeric() || c == '_' || c == '-')
    {
        return Err("Project name must be alphanumeric and cannot be empty.".to_string());
    }

    // Group ID input with validation
    let group_id = match &args.group_id {
        Some(id) => id.to_string(),
        None => arguments_provider
            .group_id
            .get_arguments()
            .map_err(|e| e.to_string())?,
    };

    // Validate the group ID format
    let group_id_regex = Regex::new(r"^[a-zA-Z][a-zA-Z0-9]*(\.[a-zA-Z][a-zA-Z0-9]*)*$")
        .expect("Failed to compile regex for group ID validation");
    if !group_id_regex.is_match(&group_id) {
        return Err("Group ID must be in the format com.example.myapp.".to_string());
    }

    // Version input with validation and default value
    let version = match &args.version {
        Some(v) => v.to_string(),
        None => arguments_provider
            .version
            .get_arguments()
            .map_err(|e| e.to_string())?,
    };

    // Validate the version format
    let version_regex =
        Regex::new(r"^\d+\.\d+\.\d+$").expect("Failed to compile regex for version validation");

    if !version_regex.is_match(&version) {
        return Err("Version must be in the format X.Y.Z (e.g., 1.0.0).".to_string());
    }

    // Java target version selection
    let java_target = match &args.java_target {
        Some(target) => {
            // Validate the provided target version
            if !["1.8", "11", "17", "21"].contains(&target.as_str()) {
                return Err(
                    "Invalid Java target version. Supported versions are 1.8, 11, 17, and 21."
                        .to_string(),
                );
            }
            target.to_string()
        }
        None => arguments_provider
            .java_target
            .get_arguments()
            .map_err(|e| e.to_string())?,
    };

    // Build engine selection
    let engine = match &args.engine {
        Some(e) => {
            // Validate the provided engine
            if !["maven", "gradle"].contains(&e.as_str()) {
                return Err(
                    "Invalid build engine. Supported engines are maven and gradle.".to_string(),
                );
            }
            e.to_string()
        }
        None => arguments_provider
            .engine
            .get_arguments()
            .map_err(|e| e.to_string())?,
    };

    if engine != "maven" {
        return Err("Currently, only Maven is supported for console applications.".to_string());
    }

    create_maven_console_app(templates::CreateConsole {
        group_id,
        version,
        java_target,
        name,
    })
}
