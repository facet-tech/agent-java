package main.java.agent;

import java.util.HashMap;
import java.util.Map;

public class Breaker {
    private String circuitBreaker;
    private String signatureReturnType;
    private Map<String,String> parameterMapping;

    public Breaker() {
        parameterMapping = new HashMap<>();
    }

    public String getCircuitBreaker() {
        return circuitBreaker;
    }

    public void setCircuitBreaker(String circuitBreaker) {
        this.circuitBreaker = circuitBreaker;
    }

    public String getSignatureReturnType() {
        return signatureReturnType;
    }

    public void setSignatureReturnType(String signatureReturnType) {
        this.signatureReturnType = signatureReturnType;
    }

    public Map<String, String> getParameterMapping() {
        return parameterMapping;
    }

    public void setParameterMapping(Map<String, String> parameterMapping) {
        this.parameterMapping = parameterMapping;
    }

    public void addParameter(String key, String value) {
        this.parameterMapping.put(key,value);
    }
}
