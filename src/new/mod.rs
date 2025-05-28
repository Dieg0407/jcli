use clap::{Args, Subcommand};

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
                println!("Creating a new console application! {:?}", args);
                // Handle the console command with args
                Ok(())
            }
        }
    }
}
