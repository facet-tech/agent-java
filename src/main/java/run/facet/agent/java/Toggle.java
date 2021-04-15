package run.facet.agent.java;

import java.util.HashMap;
import java.util.Map;

public class Toggle {
    public Method method;
    public Map<String, String> parameterMapping;

    public Toggle(Method method) {
        this.method = method;
        this.parameterMapping = new HashMap<>();
    }

    public Toggle() {
        parameterMapping = new HashMap<>();
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Map<String, String> getParameterMapping() {
        return parameterMapping;
    }

    public void setParameterMapping(Map<String, String> parameterMapping) {
        this.parameterMapping = parameterMapping;
    }
}
