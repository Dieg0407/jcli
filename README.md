# About
This project should contain a cli application that can serve as an equivalent for the `dotnet` cli tool
we have on .Net core.

The idea is to have a simple tool that can generate some project templates based on maven/gradle using
the spring folder convention. That being said a `src` folder with `main` and `test`.

Maven will be supported first as it should be easier to handle xml rather than a language like groovy or 
kotlin which would be needed for gradle.

## Platform dependencies
- `Java 21`: Temurin was used to create this application

## Roadmap
- Maven support for simple console app ⏳
- Add a bash | powershell wrapper ⏳
- Maven support for adding/removing dependencies ⏳
- Maven support for configuring some "common" plugins ⏳
- Maven support for deployable webapps ⏳
- Gradle support for simple console app ⏳ 
- Gradle support for adding/removing dependencies ⏳
- Gradle support for configuring some default plugins ⏳
- Gradle support for deployable webapps ⏳
