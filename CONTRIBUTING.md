# Contributing

Thank you for contributing to the project! Please read our [code of conduct](./CODE_OF_CONDUCT.md) before you get started with the project setup.

## IntelliJ IDEA setup

We recommend using IntelliJ IDEA for development on this project. Configure as follows:

1. Select `File > Open` and select `agent-java/build.gradle`.
1. Select `Open as Project`.
1. Wait for the builds, imports, and indexing to finish. This may take a few minutes due to the project's size and complexity.
1. Add Java 11 SDK: select `File > Project Structure... > Platform Settings > SDKs > Add New SDK`.
1. Configure project SDK and target language level: select `File > Project Structure... > Project Settings > Project`.
1. Set `Project SDK` to JDK 11
1. Set `Project language level` to 11

## Building

`./gradlew clean shadowJar`

Run the above command from the project root directory to build the java agent:

The Java agent requires JDK 11 or higher to build; your JAVA_HOME must be set to this JDK version.

After building, Java agent artifacts are located in the directory: `build/generated/libs/`
