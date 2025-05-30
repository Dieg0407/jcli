use std::path::Path;

pub struct CreateConsole {
    pub group_id: String,
    pub version: String,
    pub java_target: String,
    pub name: String,
}

pub fn create_maven_console_app(args: CreateConsole) -> Result<(), String> {
    // check if pom already exists
    let pom_path = format!("{}/pom.xml", args.name);
    if Path::new(&pom_path).exists() {
        return Err(format!("Project '{}' already exists.", args.name));
    }

    // Create the project directory
    std::fs::create_dir_all(&args.name).map_err(|e| e.to_string())?;
    std::env::set_current_dir(&args.name).map_err(|e| e.to_string())?;

    // Create the pom.xml file
    let pom_content = format!(
        r#"<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>{}</groupId>
    <artifactId>{}</artifactId>
    <version>{}</version>
    <properties>
        <maven.compiler.source>{}</maven.compiler.source>
        <maven.compiler.target>{}</maven.compiler.target>
    </properties>
    <dependencies>

    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>{}</source>
                    <target>{}</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.2.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>{}.Main</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
"#,
        args.group_id,
        args.name,
        args.version,
        args.java_target,
        args.java_target,
        args.java_target,
        args.java_target,
        args.name
    );
    std::fs::write("pom.xml", pom_content).map_err(|e| e.to_string())?;

    // Create the main directory and Main.java file
    let main_dir = format!("src/main/java/{}", args.group_id.replace('.', "/"));
    std::fs::create_dir_all(&main_dir).map_err(|e| e.to_string())?;
    let main_file_content = format!(
        r#"package {};

public class Main {{

    public static void main(String[] args) {{
        System.out.println("Hello, World!");
    }}
}}"#,
        args.group_id
    );

    std::fs::write(format!("{}/Main.java", main_dir), main_file_content)
        .map_err(|e| e.to_string())?;

    Ok(())
}
