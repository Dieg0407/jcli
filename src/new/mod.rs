mod arguments;
mod templates;
mod validation;

use arguments::{JavaProjectArgumentsProviders, default_java_arguments_providers};
use clap::{Args, Subcommand};
use templates::{CreateConsole, generate_console_app_files};
use validation::{GROUP_ID_REGEX, VALID_BUILD_ENGINES, VALID_JAVA_VERSIONS, VERSION_REGEX};

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
            NewSubcommands::Console(args) => create_console_app(
                args,
                default_java_arguments_providers(),
                generate_console_app_files,
            ),
        }
    }
}

fn create_console_app(
    args: &ConsoleArgs,
    arguments_provider: JavaProjectArgumentsProviders,
    create_console_handler: fn(&str, CreateConsole) -> Result<(), String>,
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

    if !GROUP_ID_REGEX.is_match(&group_id) {
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

    if !VERSION_REGEX.is_match(&version) {
        return Err("Version must be in the format X.Y.Z (e.g., 1.0.0).".to_string());
    }

    // Java target version selection
    let java_target = match &args.java_target {
        Some(target) => {
            // Validate the provided target version
            if !VALID_JAVA_VERSIONS.contains(&target.as_str()) {
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
            if !VALID_BUILD_ENGINES.contains(&e.as_str()) {
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

    create_console_handler(
        &engine,
        templates::CreateConsole {
            group_id,
            version,
            java_target,
            name,
        },
    )
}

#[cfg(test)]
mod tests {
    use crate::new::{
        ConsoleArgs, CreateConsole,
        arguments::{ArgumentProvider, JavaProjectArgumentsProviders},
        create_console_app,
    };
    use std::cell::RefCell;
    use std::rc::Rc;

    struct MockArgumentProvider {
        called: Rc<RefCell<bool>>,
        return_value: String,
    }

    impl MockArgumentProvider {
        fn new(called: Rc<RefCell<bool>>, return_value: &str) -> Self {
            Self {
                called,
                return_value: return_value.to_string(),
            }
        }
    }

    impl ArgumentProvider<String> for MockArgumentProvider {
        fn get_arguments(&self) -> Result<String, String> {
            *self.called.borrow_mut() = true;
            Ok(self.return_value.clone())
        }
    }

    struct FailingMockArgumentProvider {
        called: Rc<RefCell<bool>>,
        error_message: String,
    }

    impl FailingMockArgumentProvider {
        fn new(called: Rc<RefCell<bool>>, error_message: &str) -> Self {
            Self {
                called,
                error_message: error_message.to_string(),
            }
        }
    }

    impl ArgumentProvider<String> for FailingMockArgumentProvider {
        fn get_arguments(&self) -> Result<String, String> {
            *self.called.borrow_mut() = true;
            Err(self.error_message.clone())
        }
    }

    fn mock_console_handler(_engine: &str, _console: CreateConsole) -> Result<(), String> {
        Ok(())
    }

    fn failing_console_handler(_engine: &str, _console: CreateConsole) -> Result<(), String> {
        Err("Failed to create console application".to_string())
    }

    fn create_mock_providers() -> (
        JavaProjectArgumentsProviders,
        Rc<RefCell<bool>>,
        Rc<RefCell<bool>>,
        Rc<RefCell<bool>>,
        Rc<RefCell<bool>>,
        Rc<RefCell<bool>>,
    ) {
        let name_called = Rc::new(RefCell::new(false));
        let group_id_called = Rc::new(RefCell::new(false));
        let version_called = Rc::new(RefCell::new(false));
        let java_target_called = Rc::new(RefCell::new(false));
        let engine_called = Rc::new(RefCell::new(false));

        let providers = JavaProjectArgumentsProviders {
            name: Box::new(MockArgumentProvider::new(name_called.clone(), "test-app")),
            group_id: Box::new(MockArgumentProvider::new(
                group_id_called.clone(),
                "com.example",
            )),
            version: Box::new(MockArgumentProvider::new(version_called.clone(), "1.0.0")),
            java_target: Box::new(MockArgumentProvider::new(java_target_called.clone(), "17")),
            engine: Box::new(MockArgumentProvider::new(engine_called.clone(), "maven")),
        };

        (
            providers,
            name_called,
            group_id_called,
            version_called,
            java_target_called,
            engine_called,
        )
    }

    #[test]
    fn should_use_provided_arguments_when_all_arguments_are_specified() {
        // Given
        let args = ConsoleArgs {
            name: Some("my-app".to_string()),
            group_id: Some("com.example".to_string()),
            version: Some("1.0.0".to_string()),
            java_target: Some("17".to_string()),
            engine: Some("maven".to_string()),
        };

        let (
            providers,
            name_called,
            group_id_called,
            version_called,
            java_target_called,
            engine_called,
        ) = create_mock_providers();

        // When
        let result = create_console_app(&args, providers, mock_console_handler);

        // Then
        assert!(result.is_ok());
        assert_eq!(*name_called.borrow(), false); // Should not call name provider
        assert_eq!(*group_id_called.borrow(), false); // Should not call group_id provider
        assert_eq!(*version_called.borrow(), false); // Should not call version provider
        assert_eq!(*java_target_called.borrow(), false); // Should not call java_target provider
        assert_eq!(*engine_called.borrow(), false); // Should not call engine provider
    }

    #[test]
    fn should_use_argument_provider_when_arguments_are_not_provided() {
        // Given
        let args = ConsoleArgs {
            name: None,
            group_id: None,
            version: None,
            java_target: None,
            engine: None,
        };

        let (
            providers,
            name_called,
            group_id_called,
            version_called,
            java_target_called,
            engine_called,
        ) = create_mock_providers();

        // When
        let result = create_console_app(&args, providers, mock_console_handler);

        // Then
        assert!(result.is_ok());
        assert_eq!(*name_called.borrow(), true); // Should call name provider
        assert_eq!(*group_id_called.borrow(), true); // Should call group_id provider
        assert_eq!(*version_called.borrow(), true); // Should call version provider
        assert_eq!(*java_target_called.borrow(), true); // Should call java_target provider
        assert_eq!(*engine_called.borrow(), true); // Should call engine provider
    }

    #[test]
    fn should_use_mix_of_provided_arguments_and_providers() {
        // Given
        let args = ConsoleArgs {
            name: Some("my-app".to_string()),
            group_id: None,
            version: Some("1.0.0".to_string()),
            java_target: None,
            engine: Some("maven".to_string()),
        };

        let (
            providers,
            name_called,
            group_id_called,
            version_called,
            java_target_called,
            engine_called,
        ) = create_mock_providers();

        // When
        let result = create_console_app(&args, providers, mock_console_handler);

        // Then
        assert!(result.is_ok());
        assert_eq!(*name_called.borrow(), false); // Should not call name provider
        assert_eq!(*group_id_called.borrow(), true); // Should call group_id provider
        assert_eq!(*version_called.borrow(), false); // Should not call version provider
        assert_eq!(*java_target_called.borrow(), true); // Should call java_target provider
        assert_eq!(*engine_called.borrow(), false); // Should not call engine provider
    }

    #[test]
    fn should_validate_project_name() {
        // Given
        let args = ConsoleArgs {
            name: Some("invalid name with spaces".to_string()),
            group_id: Some("com.example".to_string()),
            version: Some("1.0.0".to_string()),
            java_target: Some("17".to_string()),
            engine: Some("maven".to_string()),
        };

        let (providers, _, _, _, _, _) = create_mock_providers();

        // When
        let result = create_console_app(&args, providers, mock_console_handler);

        // Then
        assert!(result.is_err());
        assert_eq!(
            result.unwrap_err(),
            "Project name must be alphanumeric and cannot be empty."
        );
    }

    #[test]
    fn should_validate_group_id() {
        // Given
        let args = ConsoleArgs {
            name: Some("valid-name".to_string()),
            group_id: Some("invalid group id".to_string()),
            version: Some("1.0.0".to_string()),
            java_target: Some("17".to_string()),
            engine: Some("maven".to_string()),
        };

        let (providers, _, _, _, _, _) = create_mock_providers();

        // When
        let result = create_console_app(&args, providers, mock_console_handler);

        // Then
        assert!(result.is_err());
        assert_eq!(
            result.unwrap_err(),
            "Group ID must be in the format com.example.myapp."
        );
    }

    #[test]
    fn should_validate_version() {
        // Given
        let args = ConsoleArgs {
            name: Some("valid-name".to_string()),
            group_id: Some("com.example".to_string()),
            version: Some("invalid version".to_string()),
            java_target: Some("17".to_string()),
            engine: Some("maven".to_string()),
        };

        let (providers, _, _, _, _, _) = create_mock_providers();

        // When
        let result = create_console_app(&args, providers, mock_console_handler);

        // Then
        assert!(result.is_err());
        assert_eq!(
            result.unwrap_err(),
            "Version must be in the format X.Y.Z (e.g., 1.0.0)."
        );
    }

    #[test]
    fn should_validate_java_target() {
        // Given
        let args = ConsoleArgs {
            name: Some("valid-name".to_string()),
            group_id: Some("com.example".to_string()),
            version: Some("1.0.0".to_string()),
            java_target: Some("invalid-java-version".to_string()),
            engine: Some("maven".to_string()),
        };

        let (providers, _, _, _, _, _) = create_mock_providers();

        // When
        let result = create_console_app(&args, providers, mock_console_handler);

        // Then
        assert!(result.is_err());
        assert_eq!(
            result.unwrap_err(),
            "Invalid Java target version. Supported versions are 1.8, 11, 17, and 21."
        );
    }

    #[test]
    fn should_validate_build_engine() {
        // Given
        let args = ConsoleArgs {
            name: Some("valid-name".to_string()),
            group_id: Some("com.example".to_string()),
            version: Some("1.0.0".to_string()),
            java_target: Some("17".to_string()),
            engine: Some("invalid-engine".to_string()),
        };

        let (providers, _, _, _, _, _) = create_mock_providers();

        // When
        let result = create_console_app(&args, providers, mock_console_handler);

        // Then
        assert!(result.is_err());
        assert_eq!(
            result.unwrap_err(),
            "Invalid build engine. Supported engines are maven and gradle."
        );
    }

    #[test]
    fn should_reject_gradle_engine_for_now() {
        // Given
        let args = ConsoleArgs {
            name: Some("valid-name".to_string()),
            group_id: Some("com.example".to_string()),
            version: Some("1.0.0".to_string()),
            java_target: Some("17".to_string()),
            engine: Some("gradle".to_string()),
        };

        let (providers, _, _, _, _, _) = create_mock_providers();

        // When
        let result = create_console_app(&args, providers, mock_console_handler);

        // Then
        assert!(result.is_err());
        assert_eq!(
            result.unwrap_err(),
            "Currently, only Maven is supported for console applications."
        );
    }

    #[test]
    fn should_handle_failing_argument_provider() {
        // Given
        let args = ConsoleArgs {
            name: Some("valid-name".to_string()),
            group_id: None,
            version: Some("1.0.0".to_string()),
            java_target: Some("17".to_string()),
            engine: Some("maven".to_string()),
        };

        let group_id_called = Rc::new(RefCell::new(false));

        let providers = JavaProjectArgumentsProviders {
            name: Box::new(MockArgumentProvider::new(
                Rc::new(RefCell::new(false)),
                "test-app",
            )),
            group_id: Box::new(FailingMockArgumentProvider::new(
                group_id_called.clone(),
                "Failed to get group ID",
            )),
            version: Box::new(MockArgumentProvider::new(
                Rc::new(RefCell::new(false)),
                "1.0.0",
            )),
            java_target: Box::new(MockArgumentProvider::new(
                Rc::new(RefCell::new(false)),
                "17",
            )),
            engine: Box::new(MockArgumentProvider::new(
                Rc::new(RefCell::new(false)),
                "maven",
            )),
        };

        // When
        let result = create_console_app(&args, providers, mock_console_handler);

        // Then
        assert!(result.is_err());
        assert_eq!(result.unwrap_err(), "Failed to get group ID");
        assert_eq!(*group_id_called.borrow(), true); // Should call group_id provider
    }

    #[test]
    fn should_handle_failing_console_handler() {
        // Given
        let args = ConsoleArgs {
            name: Some("valid-name".to_string()),
            group_id: Some("com.example".to_string()),
            version: Some("1.0.0".to_string()),
            java_target: Some("17".to_string()),
            engine: Some("maven".to_string()),
        };

        let (providers, _, _, _, _, _) = create_mock_providers();

        // When
        let result = create_console_app(&args, providers, failing_console_handler);

        // Then
        assert!(result.is_err());
        assert_eq!(result.unwrap_err(), "Failed to create console application");
    }
}
