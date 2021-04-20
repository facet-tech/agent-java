# Facet java-agent
**Dynamically enable and disable methods and endpoints**

[![Github](https://github.com/facet-tech/agent-java/actions/workflows/main.yml/badge.svg)](https://github.com/facet-tech/agent-java/actions/workflows/main.yml/badge.svg)


> The Facet Java Agent uses bytecode instrumentation to dynamically enable and disable methods at runtime within your application.
> This is achieved by transferring control back to the caller of the method via injected return statements.
> After integration you can toggle methods and endpoints in realtime without modifying or restarting you application.

## Download
The facet agent jar can be downloaded from [maven central](https://search.maven.org/artifact/run.facet.agent.java/facet-agent).
```
<dependency>
    <groupId>run.facet.agent.java</groupId>
    <artifactId>facet-agent</artifactId>
</dependency>
```

## Install
1. Configure your JVM to load the agent during your application's premain start-up by passing the `-javaagent:/full/path/to/facet.jar` command-line argument.
```
java -javaagent:/full/path/to/facet.jar -jar /full/path/to/application
```

2. Create a `facet.yml` file located in the same directory as the `facet-agent.jar` from step 1.

```
workspaceId: {WORKSPACE_ID}
name: {APPLICAITON_NAME}
environment: {ENVIRONMENT}
apiKey: {API_KEY}
```

`workspaceId` is retrieved from the [dashboard](http://app.facet.run/)

`apiKey` is retrieved from the [dashboard](http://app.facet.run/)

`name` is the name of your application

`environment` is the environment of your application deployment

## Troubleshooting
1. If you encounter `javax.management.InstanceAlreadyExistsException` while running locally *disable* JMX integration in your IDE.



## Usage Cases
We are in the early stages of product development and can envision several use cases for this type of solution.   
1. Endpoint toggles - disable or reroute endpoints on the fly for beta features or when application and performance issues arise.
1. Chaos engineering - automated chaos engineering testing framework.
1. Logging toggles - increase and  decrease logging or add and remove log statements dynamically without deployment and restarting applications.
1. Low code feature flags - low code alternative to traditional feature flag toggles.

We would love to hear any feedback (positive and negative) or other use case ideas.  The future direction of facet will be directly impacted by YOUR feedback!

## Circuit Breakers

Method toggles are achieved by stopping method execution and transferring control back to the caller via injected return statements called circuit breakers.  Below are the default return values for method return types.

```
METHOD_RETURN_TYPE  VALUE
------------------  -----------------------
              byte  Byte.MIN_VALUE
             short  Short.MIN_VALUE
               int  Integer.MIN_VALUE
              long  Long.MIN_VALUE
             float  Float.MIN_VALUE
            double  Double.MIN_VALUE
              char  Character.MIN_VALUE
           boolean  false
              void  void
             other  null
```
Circuit breakers are data driven and will soon be configurable.

Circuit breakers are stored in the directory `db/configuration/circuit_breakers`.

## Frameworks

Frameworks are a combination circuit breakers which detect annotations, interfaces, and inheritance to customize the return value (response) creating support for HTTP requests and endpoints.  If a framework is detected, the following return values will be used instead of the default circuit breaker mapping.

Currently, we support Spring Framework version 3.0.x and higher via the following spring annotations.

```
ANNOTATION_CLASS                                        RETURN VALUE
------------------------------------------------------  -----------------
org.springframework.web.bind.annotation.RequestMapping  javax.servlet.http.HttpServletResponse.sendError(403,"Access Denied")
    org.springframework.web.bind.annotation.GetMapping  javax.servlet.http.HttpServletResponse.sendError(403,"Access Denied")
   org.springframework.web.bind.annotation.PostMapping  javax.servlet.http.HttpServletResponse.sendError(403,"Access Denied")
    org.springframework.web.bind.annotation.PutMapping  javax.servlet.http.HttpServletResponse.sendError(403,"Access Denied")
 org.springframework.web.bind.annotation.DeleteMapping  javax.servlet.http.HttpServletResponse.sendError(403,"Access Denied")
  org.springframework.web.bind.annotation.PatchMapping  javax.servlet.http.HttpServletResponse.sendError(403,"Access Denied")
```
Frameworks are data driven and will soon be configurable.

Frameworks are stored in the directory `db/configuration/configuration/frameworks`.

## Block List
A list of packages and classes not processed preventing method and endpoint toggle overload.

See [block list.](db/configuration/block_list/default.json)

Block lists are data driven and will soon be configurable.

Block lists are stored in the directory `db/configuration/circuit_breakers`.

## Building

`./gradlew clean shadowJar`

Run the above command from the project root directory to build the java agent:

The Java agent requires JDK 11 or higher to build; your JAVA_HOME must be set to this JDK version.

After building, Java agent artifacts are located in the directory: `build/generated/libs/`

## IntelliJ IDEA setup

We recommend using IntelliJ IDEA for development on this project. Configure as follows:

1. Select `File > Open` and select `agent-java/build.gradle`.
1. Select `Open as Project`.
1. Wait for the builds, imports, and indexing to finish. This may take a few minutes due to the project's size and complexity.
1. Add Java 11 SDK: select `File > Project Structure... > Platform Settings > SDKs > Add New SDK`.
1. Configure project SDK and target language level: select `File > Project Structure... > Project Settings > Project`.
1. Set `Project SDK` to JDK 11
1. Set `Project language level` to 11


## Contributing

Thank you for contributing to the project! Please read the [CONTRIBUTING.md](./CONTRIBUTING.md) file to get started with the Facet Java-Agent project!

## Feedback and Support

Open an [issue](https://github.com/facet-tech/agent-java/issues) or send email to `engineering@facet.run`.

## Compatibility
```
Java:  JVM runtime 11 or higher is required, however, your source and target can be compiled to any java version.
```

## License

[MIT](./LICENSE)