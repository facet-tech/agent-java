package run.facet.agent.java;

import javassist.bytecode.AnnotationsAttribute;

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

    public String getVisibilityString(String visibility) {
        String visibilityString;
        switch (visibility) {
            case "run.facet.dependencies.javassist.bytecode.AnnotationsAttribute.visibleTag":
                visibilityString = AnnotationsAttribute.visibleTag;
                break;
            case "run.facet.dependencies.javassist.bytecode.AccessFlag.Modifier.invisibleTag":
                visibilityString = AnnotationsAttribute.invisibleTag;
                break;
            default:
                visibilityString = null;
        }
        return visibilityString;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
}

