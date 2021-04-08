package run.facet.agent.java;

import java.util.ArrayList;
import java.util.List;

public class Annotation {
    private String className;
    private List<Parameter> parameters;
    private String visibility;


    public Annotation() {
        parameters = new ArrayList<>();
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(Parameter parameter) {
        parameters.add(parameter);
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
}

