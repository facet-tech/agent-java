package run.facet.agent.java;

import java.util.ArrayList;
import java.util.List;

public class Method {
    private List<Annotation> annotations;
    private String body;
    private List<Exception> exceptions;
    private String modifier;
    private String name;
    private List<Parameter> parameters;
    private String returnType;

    public Method() {
        this.annotations = new ArrayList<>();
        this.exceptions = new ArrayList<>();
        this.parameters = new ArrayList<>();
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<Exception> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<Exception> exceptions) {
        this.exceptions = exceptions;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }
}

