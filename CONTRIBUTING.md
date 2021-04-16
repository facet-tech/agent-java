# Contributing

Thank you for contributing to the project! Please read our [code of conduct](./CODE_OF_CONDUCT.md) before you get started with the project setup.

## Terminology

- Sensor: Adds the ability to classify methods as endpoints. 
- Circuit Braker (CB): Custom logic to stop method execution
- Framework = custom implementation of a circuit braker.
- Precedence = Order of CB. This is crucial when more than 1 CB is in effect.
- Response = search for the method to that parameter type `javax.servlet.http.HttpServletResponse`

Note that CB triggers `@ExceptionHandler`  everytime a method is turned off.

Current Payload is stored in the DB:

```
{
  "attribute": {
    "circuitBreakers": [
      {
        "precedence": 1,
        "toggle": {
          "method": {
            "body": "if(!${toggle}) {try { ${response}.sendError(403,\"Access Denied\");  } catch (Exception e) { } return null;}"
          },
          "parameterMapping": {
            "response": "javax.servlet.http.HttpServletResponse"
          }
        }
      },
      {
        "methodsToCreate": [
          {
            "annotations": [
              {
                "className": "org.springframework.web.bind.annotation.ExceptionHandler",
                "parameters": [
                  {
                    "className": "run.facet.dependencies.javassist.bytecode.annotation.ArrayMemberValue",
                    "name": "value",
                    "type": "list",
                    "values": [
                      {
                        "className": "run.facet.dependencies.javassist.bytecode.annotation.ClassMemberValue",
                        "type": "string",
                        "value": "run.facet.agent.java.CircuitBreakerException"
                      }
                    ]
                  }
                ],
                "visibility": "run.facet.dependencies.javassist.bytecode.AnnotationsAttribute.visibleTag"
              }
            ],
            "body": "try { $3.sendError(403,\"Access Denied\"); } catch (Exception e) {}",
            "modifier": "run.facet.dependencies.javassist.bytecode.AccessFlag.Modifier.PUBLIC",
            "name": "handleFacetRunCircuitBreakerException",
            "parameters": [
              {
                "className": "run.facet.agent.java.CircuitBreakerException"
              },
              {
                "className": "javax.servlet.http.HttpServletRequest"
              },
              {
                "className": "javax.servlet.http.HttpServletResponse"
              }
            ],
            "returnType": "run.facet.dependencies.javassist.CtClass.voidType"
          }
        ],
        "precedence": 2,
        "toggle": {
          "method": {
            "body": "if(!${toggle}) {throw new run.facet.agent.java.CircuitBreakerException();}",
            "exceptions": [
              {
                "className": "run.facet.agent.java.CircuitBreakerException"
              }
            ]
          }
        }
      }
    ],
    "name": "Spring",
    "sensors": [
      {
        "annotations": [
          {
            "className": "org.springframework.web.bind.annotation.RequestMapping",
            "parameters": [
              {
                "name": "consumes"
              },
              {
                "name": "headers"
              },
              {
                "name": "name"
              },
              {
                "name": "params"
              },
              {
                "name": "path"
              },
              {
                "name": "produces"
              },
              {
                "name": "value"
              }
            ]
          },
          {
            "className": "org.springframework.web.bind.annotation.GetMapping",
            "parameters": [
              {
                "name": "consumes"
              },
              {
                "name": "headers"
              },
              {
                "name": "name"
              },
              {
                "name": "params"
              },
              {
                "name": "path"
              },
              {
                "name": "produces"
              },
              {
                "name": "value"
              }
            ]
          },
          ... (support of PostMapping, DeleteMapping and PatchMapping)
        ]
      }
    ],
    "version": "0.0.1"
  },
  "id": "JAVA~1",
  "property": "FRAMEWORK~"
}
```
