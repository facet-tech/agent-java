# Contributing

Thank you for contributing to the project! Please read our [code of conduct](./CODE_OF_CONDUCT.md) before you get started with the project setup.

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
ANNOTATION_CLASS                                        RETURN VALUE
------------------------------------------------------  -----------------
org.springframework.web.bind.annotation.RequestMapping  javax.servlet.http.HttpServletResponse.sendError(403,"Access Denied")
org.springframework.web.bind.annotation.GetMapping      javax.servlet.http.HttpServletResponse.sendError(403,"Access Denied")
org.springframework.web.bind.annotation.PostMapping     javax.servlet.http.HttpServletResponse.sendError(403,"Access Denied")
org.springframework.web.bind.annotation.PutMapping      javax.servlet.http.HttpServletResponse.sendError(403,"Access Denied")
org.springframework.web.bind.annotation.DeleteMapping   javax.servlet.http.HttpServletResponse.sendError(403,"Access Denied")
org.springframework.web.bind.annotation.PatchMapping    javax.servlet.http.HttpServletResponse.sendError(403,"Access Denied")
```
Frameworks are data driven and will soon be configurable.

Frameworks are stored in this [directory](db/configuration/frameworks).

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
