# About
This project should contain a cli application that can serve as an equivalent for the `dotnet` cli tool
we have on .Net core.

The idea is to have a simple tool that can generate some project templates based on maven/gradle using
the spring folder convention. That being said a `src` folder with `main` and `test`.

Maven will be supported first as it should be easier to handle xml rather than a language like groovy or 
kotlin which would be needed for gradle.

## Platform dependencies
- [OpenJDK 17 Temurin](https://adoptium.net/temurin/releases/?version=17)
- [Node 20](https://nodejs.org/en/download/)
- [Husky](https://typicode.github.io/husky/#/)
- [semver-next](https://github.com/Dieg0407/semver-next)

## Roadmap
- Maven support for simple console app ✅
- Add CI/CD pipeline ⏳
- Add a bash | powershell wrapper ⏳
- Maven support for adding/removing dependencies ⏳
- Maven support for configuring some "common" plugins ⏳
- Maven support for deployable webapps ⏳
- Gradle support for simple console app ⏳ 
- Gradle support for adding/removing dependencies ⏳
- Gradle support for configuring some default plugins ⏳
- Gradle support for deployable webapps ⏳
