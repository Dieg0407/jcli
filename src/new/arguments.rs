use dialoguer::{Input, Select, theme::ColorfulTheme};

pub trait ArgumentProvider<T> {
    fn get_arguments(&self) -> Result<T, String>;
}

pub fn default_java_arguments_providers() -> JavaProjectArgumentsProviders {
    JavaProjectArgumentsProviders {
        group_id: Box::new(GroupIdArgumentProvider),
        version: Box::new(VersionArgumentProvider),
        java_target: Box::new(JavaTargetArgumentProvider),
        engine: Box::new(EngineArgumentProvider),
        name: Box::new(NameArgumentProvider),
    }
}

pub struct JavaProjectArgumentsProviders {
    pub group_id: Box<dyn ArgumentProvider<String>>,
    pub version: Box<dyn ArgumentProvider<String>>,
    pub java_target: Box<dyn ArgumentProvider<String>>,
    pub engine: Box<dyn ArgumentProvider<String>>,
    pub name: Box<dyn ArgumentProvider<String>>,
}

struct NameArgumentProvider;
struct GroupIdArgumentProvider;
struct VersionArgumentProvider;
struct JavaTargetArgumentProvider;
struct EngineArgumentProvider;

impl ArgumentProvider<String> for NameArgumentProvider {
    fn get_arguments(&self) -> Result<String, String> {
        let name = Input::with_theme(&ColorfulTheme::default())
            .with_prompt("Application name")
            .interact()
            .map_err(|e| e.to_string())?;
        Ok(name)
    }
}

impl ArgumentProvider<String> for GroupIdArgumentProvider {
    fn get_arguments(&self) -> Result<String, String> {
        let group_id = Input::with_theme(&ColorfulTheme::default())
            .with_prompt("Group ID for Java package")
            .interact()
            .map_err(|e| e.to_string())?;
        Ok(group_id)
    }
}

impl ArgumentProvider<String> for VersionArgumentProvider {
    fn get_arguments(&self) -> Result<String, String> {
        let version = Input::with_theme(&ColorfulTheme::default())
            .with_prompt("Version")
            .default("1.0.0".to_string())
            .interact()
            .map_err(|e| e.to_string())?;
        Ok(version)
    }
}

impl ArgumentProvider<String> for JavaTargetArgumentProvider {
    fn get_arguments(&self) -> Result<String, String> {
        let java_versions = &["1.8", "11", "17", "21"];
        let selection = Select::with_theme(&ColorfulTheme::default())
            .with_prompt("Select Java target version")
            .default(3) // Default to Java 21 (index 3)
            .items(java_versions)
            .interact()
            .map_err(|e| e.to_string())?;

        Ok(java_versions[selection].to_string())
    }
}

impl ArgumentProvider<String> for EngineArgumentProvider {
    fn get_arguments(&self) -> Result<String, String> {
        let options = &["maven", "gradle"];
        let selection = Select::with_theme(&ColorfulTheme::default())
            .with_prompt("Select build engine")
            .default(0) // Default to maven (first option)
            .items(options)
            .interact()
            .map_err(|e| e.to_string())?;

        Ok(options[selection].to_string())
    }
}
