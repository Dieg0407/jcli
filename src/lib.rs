use clap::{Parser, Subcommand};

mod new;

use new::NewCommand;

#[derive(Debug, Parser)]
#[clap(author, version, about, long_about = None)]
pub struct Cli {
    #[clap(subcommand)]
    pub command: Commands,
}

#[derive(Debug, Subcommand)]
pub enum Commands {
    New(NewCommand),
}

impl Cli {
    pub fn run(&self) -> Result<(), String> {
        match &self.command {
            Commands::New(new_cmd) => new_cmd.execute(),
        }
    }
}
