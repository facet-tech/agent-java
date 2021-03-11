package run.facet.agent.java;

import java.util.HashMap;
import java.util.Map;

public class Annotation {
    private String name;
    private Map<String, String> parameters;
    private Breaker circuitBreaker;

    public Annotation() {
        parameters = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public Breaker getCircuitBreaker() {
        return circuitBreaker;
    }

    public void setCircuitBreaker(Breaker circuitBreaker) {
        this.circuitBreaker = circuitBreaker;
    }

    public void addParameter(String name, String value) {
        parameters.put(name,value);
    }
}

