use clap::Parser;
use jcli::Cli;

fn main() {
    let cli = Cli::parse();
    if let Err(e) = cli.run() {
        eprintln!("Error: {}", e);
        std::process::exit(1);
    }
}
