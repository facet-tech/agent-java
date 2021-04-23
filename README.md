# Overview
<div align="center">
    <a href='https://github.com/facet-tech/agent-java'>
        <img alt="Facet logo" width="40%" src="/images/facet_logo_combo.svg">
        <br/>
    </a>
    <b>Toggle methods and endpoints instantly</b>
</div>
<br/>
<br/>

[![Github](https://github.com/facet-tech/agent-java/actions/workflows/main.yml/badge.svg)](https://github.com/facet-tech/agent-java/actions/workflows/main.yml/badge.svg)
![License](https://img.shields.io/github/license/facet-tech/agent-java)

> The Facet Java Agent uses bytecode instrumentation to dynamically enable and disable methods at runtime within your application.
> This is achieved by transferring control back to the caller of the method via injected return statements.
> After integration, you can toggle methods and endpoints in realtime without modifying or restarting you application.

## Download
The facet agent jar can be downloaded from [maven central](https://search.maven.org/artifact/run.facet.agent.java/facet-agent).

## Install
1. Configure your JVM to load the agent during your application's premain start-up by passing the `-javaagent:/full/path/to/facet.jar` command-line argument. 
 
Note: Make sure you pass the **absolute path** to the JAR.

2. Create a `facet.yml` file located in the **same directory** as the `facet-agent.jar` from step 1.

```
apiKey:         {API_KEY}
workspaceId:    {WORKSPACE_ID}
name:           {APPLICAITON_NAME}
environment:    {ENVIRONMENT}
```

Grab your credentials (`workspaceId` and `apiKey`) from the Facet Dashboard at [https://app.facet.run](https://app.facet.run).

`workspaceId` The ID of the workspace.

`apiKey` Used for Facet API authentication.

`name` The name of your application.

`environment` (optional) The environment of your application deployment.

After you start your application, you will be able to preview the project listed in the dashboard, under _"Applications"_.

## Troubleshooting
1. If you encounter `javax.management.InstanceAlreadyExistsException` while running locally *disable* JMX integration in your IDE

## Usage Cases

1. Endpoint management - disable or reroute endpoints on the fly for beta features or when application and performance issues arise.
2. Chaos engineering - automated chaos engineering testing framework.
3. Logging toggles - increase and  decrease logging or add and remove log statements dynamically without deployment and restarting applications.
4. Automatic documentation generation, as well as endpoint detection.
5. Low code feature flags - low code alternative to traditional feature flag toggles.

We would love to hear your feedback!

## Feedback and Support

Open an [issue](https://github.com/facet-tech/agent-java/issues) or send an email at `engineering@facet.run`.

## Circuit Breakers

Method toggles are achieved by stopping method execution and transferring control back to the caller via injected return statements called circuit breakers.  Below are the default return values for method return types.

```
METHOD_RETURN_TYPE  VALUE
------------------  -----------------------
byte                Byte.MIN_VALUE
short               Short.MIN_VALUE
int                 Integer.MIN_VALUE
long                Long.MIN_VALUE
float               Float.MIN_VALUE
double              Double.MIN_VALUE
char                Character.MIN_VALUE
boolean             false
void                void
other               null
```
Circuit breakers are data driven and will soon be configurable.

Circuit breakers are stored in this [directory](db/configuration/circuit_breakers).

## Frameworks

Frameworks are a combination circuit breakers which detect annotations, interfaces, and inheritance to customize the return value (response) creating support for HTTP requests and endpoints.  If a framework is detected, the following return values will be used instead of the default circuit breaker mapping.

Currently, we support Spring Framework version 3.0.x and higher via the following spring annotations.

```
ANNOTATION_CLASS                                RETURN VALUE
(org.springframework.web.bind.annotation)       (javax.servlet.http.HttpServletResponse)
-----------------------------------------       -----------------
RequestMapping                                  sendError(403,"Access Denied")
GetMapping                                      sendError(403,"Access Denied")
PostMapping                                     sendError(403,"Access Denied")
PutMapping                                      sendError(403,"Access Denied")
DeleteMapping                                   sendError(403,"Access Denied")
PatchMapping                                    sendError(403,"Access Denied")
```
Frameworks are data driven and will soon be configurable.

Frameworks are stored in this [directory](db/configuration/frameworks).

## Block List
A list of packages and classes not processed preventing method and endpoint toggle overload.

See [block list.](db/configuration/block_list/default.json)

Block lists are data driven and will soon be configurable.

Block lists are stored in this [directory](https://github.com/facet-tech/agent-java/tree/main/db/configuration/circuit_breakers).

## Building

`./gradlew clean shadowJar`

Run the above command from the project root directory to build the java agent:

The Java agent requires JDK 11 or higher to build; your JAVA_HOME must be set to this JDK version.

After building, Java agent artifacts are located in the directory: `build/generated/libs/`

## Contributing

Thank you for contributing to the project! Please read the [CONTRIBUTING.md](./CONTRIBUTING.md) file to get started with the Facet Java-Agent project!

## Compatibility
Java:  JVM runtime 11 or higher is required, however, your source and target can be compiled to any java version.

## License

[MIT](./LICENSE)
