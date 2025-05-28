use clap::{Args, Subcommand};
use dialoguer::{Input, Select, theme::ColorfulTheme};
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
    // Project name input with validation
    let name = match &args.name {
        Some(name) => name.to_string(),
        None => {
            let name = Input::with_theme(&ColorfulTheme::default())
                .with_prompt("Application name")
                .validate_with(|input: &String| -> Result<(), &str> {
                    if !input.chars().all(|c| c.is_alphanumeric() || c == '_') {
                        Err("Only alphanumeric characters and underscores are allowed")
                    } else {
                        Ok(())
                    }
                })
                .interact()
                .map_err(|e| e.to_string())?;
            name
        }
    };

    // Group ID input with validation
    let group_id = match &args.group_id {
        Some(id) => id.to_string(),
        None => {
            let group_id_regex = Regex::new(r"^[a-zA-Z][a-zA-Z0-9]*(\.[a-zA-Z][a-zA-Z0-9]*)*$")
                .expect("Failed to compile regex for group ID validation");

            let group_id = Input::with_theme(&ColorfulTheme::default())
                .with_prompt("Group ID for Java package")
                .validate_with(|input: &String| -> Result<(), &str> {
                    if !group_id_regex.is_match(input) {
                        Err("Invalid group ID format. It should be a valid Java package name")
                    } else {
                        Ok(())
                    }
                })
                .interact()
                .map_err(|e| e.to_string())?;
            group_id
        }
    };

    // Version input with validation and default value
    let version = match &args.version {
        Some(v) => v.to_string(),
        None => {
            let version_regex = Regex::new(r"^\d+\.\d+\.\d+$")
                .expect("Failed to compile regex for version validation");

            let version = Input::with_theme(&ColorfulTheme::default())
                .with_prompt("Version")
                .default("1.0.0".to_string())
                .validate_with(|input: &String| -> Result<(), &str> {
                    if !version_regex.is_match(input) {
                        Err("Invalid version format. It should be in the format X.Y.Z (e.g., 1.0.0)")
                    } else {
                        Ok(())
                    }
                })
                .interact()
                .map_err(|e| e.to_string())?;
            version
        }
    };

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
        None => {
            let java_versions = &["1.8", "11", "17", "21"];
            let selection = Select::with_theme(&ColorfulTheme::default())
                .with_prompt("Select Java target version")
                .default(3) // Default to Java 21 (index 3)
                .items(java_versions)
                .interact()
                .map_err(|e| e.to_string())?;

            java_versions[selection].to_string()
        }
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
        None => {
            let options = &["maven", "gradle"];
            let selection = Select::with_theme(&ColorfulTheme::default())
                .with_prompt("Select build engine")
                .default(0) // Default to maven (first option)
                .items(options)
                .interact()
                .map_err(|e| e.to_string())?;

            options[selection].to_string()
        }
    };

    println!("Creating console application with the following details:");
    println!("Name: {}", name);
    println!("Group ID: {}", group_id);
    println!("Version: {}", version);
    println!("Java Target: {}", java_target);
    println!("Build Engine: {}", engine);

    Ok(())
}
