# Facet java-agent

> The Facet Java SDK can be used with the Spring Framework. The Java-Agent allows **enabling** and **disabling** methods and endpoints in your application. When a method is disabled, a default value is returned, which is usually the minimum value of the class.

This is how values are mapped:
```
RETURN_TYPE -> VALUE (if method switched OFF)

byte    ->  Byte.MIN_VALUE
short   ->  Short.MIN_VALUE
int     -> Integer.MIN_VALUE
long    -> Long.MIN_VALUE
float   -> Float.MIN_VALUE
double  -> Double.MIN_VALUE
char    -> Character.MIN_VALUE
boolean -> false
void    -> void
other   -> null
```

# Introduction

The Facet Java SDK can be used with Kotlin, Scala, and other JVM languages. Code examples are typically provided in both Java and Kotlin. On this page, we get you up and running with Facet's SDK, so that it will automatically switch methods and endpoints to enable/disable.

# Install

Facet captures data by using an SDK within your application’s runtime. Facet-agent can be found in the [sonatype distribution](https://search.maven.org/artifact/run.facet.agent.java/facet-agent).
```
<dependency>
<groupId>run.facet</groupId>
<artifactId>facet</artifactId>
<version>0.0.1</version>
</dependency>
```

# Verify

Download and add the JAR in the VM options, prior to starting your server.
`-javaagent:{JAR_PATH_VARIABLE}`

Replace JAR_PATH_VARIABLE with the path of the JAR. This will inject the Facet agent into the application. Last but not least create a facet.yml file in your project directory. The file contains your workspaceId, your project's name and your environment. You can retrieve your workspaceId by login in into the [dashboard](http://app.facet.run/).
```
workspaceId: WORKSPACE~ID
name: My-Application
environment: dev
apiKey: API_KEY
```

Navigate into the dashboard. Right after you login, select "Applications" -> "My-Application". You should be able to see all the live methods and endpoints, alongside with a checkbox allowing their enablement and disablement. You should now be able to enable/disable methods and endpoints throughout the application.

# Contributing

Thank you for contributing to the project! Please read our [code of conduct](./CODE_OF_CONDUCT.md) before you get started with the project setup.

# Support

Open an [issue](https://github.com/facet-tech/agent-java/issues) or contact the team through `engineering@facet.run`.

# License
[MIT](./LICENSE)