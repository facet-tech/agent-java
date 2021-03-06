# Overview
<div align="center">
    <a href='https://github.com/facet-tech/agent-java'>
        <img alt="Facet logo" width="60%" src="/images/facet_logo.jpg">
        <br/>
    </a>
    <b>Toggle methods and endpoints instantly</b>
</div>
<br/>
<br/>

[![Github](https://github.com/facet-tech/agent-java/actions/workflows/main.yml/badge.svg)](https://github.com/facet-tech/agent-java/actions/workflows/main.yml/badge.svg)
[![License: MIT](https://img.shields.io/badge/License-MIT-brightgreen.svg)](https://opensource.org/licenses/MIT)

> The Facet Java Agent uses the Bytecode Instrumentation API to dynamically enable and disable methods at runtime within your application.
> This is achieved by transferring control back to the caller of the method via injected return statements.
> After integration, you can toggle methods and endpoints in realtime without modifying or restarting you application.

## Download
Download the latest version of the Facet java-agent from the [maven central](https://repo1.maven.org/maven2/run/facet/agent/java/facet-agent/0.0.13/facet-agent-0.0.13.jar).

## Install
1. Move the Facet java-agent JAR in the root of the application folder, or in a subfolder in your project.
2. Configure your JVM to load the agent during your application's premain start-up by passing this command-line argument: `-javaagent:/facet-agent-VERSION.jar`. Replace `facet-ageent-VERSION.jar` with the **absolute path** of the Facet java-agent JAR. For IDEA users, this usually can be found at "Edit Configuration" -> "VM options".

3. Create a `facet.yml` file located in the **same directory** as the `facet-agent.jar` from step 1.

```
apiKey:         API_KEY
workspaceId:    WORKSPACE_ID
name:           APPLICATION_NAME
environment:    ENVIRONMENT
```
In order to retrieve `workspaceId` and `apiKey`, you need to create an account in the [Facet Dashboard](https://app.facet.run)

`apiKey` Used for Facet API authentication.  
`workspaceId` The ID of the workspace.  
`name` The name of your application.  
`environment` The environment of your application deployment. For instance, you may use `local` for your local environment.

4. Start your application and toggle endpoints and methods via the [Facet Dashboard](https://app.facet.run) -> *Applications*.

## Troubleshooting
1. In case you've encountered some warnings/error, you can troubleshoot by opening the `facet.log` file, which is generated in the _same directory_ with the facet.yml, once you start the application. Potential warnings and errors are logged in that file.
2.  If you encounter `javax.management.InstanceAlreadyExistsException` while running locally, *disable* JMX integration in your IDE

## Usage Cases

1. Endpoint management - disable or reroute endpoints on the fly for beta features or when application and performance issues arise.
2. Chaos engineering - automated chaos engineering testing framework.
3. Logging toggles - increase and  decrease logging or add and remove log statements dynamically without deployment and restarting applications.
4. Automatic documentation generation, as well as endpoint detection.
5. Low code feature flags - low code alternative to traditional feature flag toggles.

We would love to hear your feedback!

## Feedback and Support

Open an [issue](https://github.com/facet-tech/agent-java/issues) or send an email at `engineering@facet.run`.

## Technical Details

### Circuit Breakers

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
Circuit breakers are data driven and will soon be configurable. They are stored in this [directory](db/configuration/circuit_breakers).

### Frameworks

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

### Block List

A list of packages and classes not processed preventing method and endpoint toggle overload.

See [block list.](db/configuration/block_list/default.json)

Block lists are data driven and will soon be configurable.

Block lists are stored in this [directory](https://github.com/facet-tech/agent-java/tree/main/db/configuration/circuit_breakers).

## Roadmap

The future plans and high priority features and enhancements can be found in the [GitHub project board](https://github.com/orgs/facet-tech/projects/3).

## Contributing

Thank you for contributing to the project! Please read the [CONTRIBUTING.md](./CONTRIBUTING.md) file to get started with the Facet Java-Agent project!

## Compatibility
Java:  JVM runtime 11 or higher is required, however, your source and target can be compiled to any java version.

## Demo

View the [demo here](https://facet.run/video/java.mp4).

## License

[MIT](./LICENSE)
