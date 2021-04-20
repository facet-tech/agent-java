# Facet java-agent
<div>
    <img width="150" src="./images/facet_logo_combo.svg">
</div>

**Toggle methods and endpoints instantly**


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
1. If you encounter `javax.management.InstanceAlreadyExistsException` while running locally *disable* JMX integration in your IDE

## Usage Cases
We are in the early stages of product development and can envision several use cases for this type of solution.   
1. Endpoint toggles - disable or reroute endpoints on the fly for beta features or when application and performance issues arise.
1. Chaos engineering - automated chaos engineering testing framework.
1. Logging toggles - increase and  decrease logging or add and remove log statements dynamically without deployment and restarting applications.
1. Low code feature flags - low code alternative to traditional feature flag toggles.

We would love to hear any feedback (positive and negative) or other use case ideas.  The future direction of facet will be directly impacted by YOUR feedback!

## Feedback and Support

Open an [issue](https://github.com/facet-tech/agent-java/issues) or send an email at `engineering@facet.run`.

## Contributing

Thank you for contributing to the project! Please read the [CONTRIBUTING.md](./CONTRIBUTING.md) file to get started with the Facet Java-Agent project!

## Compatibility
Java:  JVM runtime 11 or higher is required, however, your source and target can be compiled to any java version.

## License

[MIT](./LICENSE)
