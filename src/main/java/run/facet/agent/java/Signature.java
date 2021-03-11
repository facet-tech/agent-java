package run.facet.agent.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Signature {
    private boolean enabled;
    private String name;
    private List<Parameter> parameter;
    private Map<String,Parameter> parameterReturnTypeMap;
    private Map<String,Parameter> parameterNameMap;
    private String returnType;
    private String signature;
    private List<Annotation> annotation;
    private Map<String,Annotation> annotationMap;

    public Signature(String name, String returnType) {
        this.name = name;
        this.returnType = returnType;
        this.parameter = new ArrayList<>();
        this.parameterNameMap = new HashMap<>();
        this.parameterReturnTypeMap = new HashMap<>();
    }

    public Signature() {}

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Parameter> getParameter() {
        return parameter;
    }

    public void setParameter(List<Parameter> parameter) {
        this.parameter = parameter;
    }

    public void addParameter(Parameter parameter) {
       this.parameter.add(parameter);
       this.parameterReturnTypeMap.put(parameter.getType(),parameter);
       this.parameterNameMap.put(parameter.getName(),parameter);
    }

    public Parameter getParameterByName(String name) {
        return this.parameterNameMap.get(name);
    }

    public Parameter getParameterByReturnType(String returnType) {
        return this.parameterReturnTypeMap.get(returnType);
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSignature() {
        if(this.signature == null) {
            String signature = this.returnType + ";" + this. name + "(";
            String params = "";
            for (Parameter parameter: this.parameter) {
                params += parameter.getType() + ",";
            }

            if (params.length() > 1) {
                params = params.substring(0,params.length() - 1);
            }

            signature += params + ")";
            this.signature = signature;
        }
        return signature;

    }

    public List<Annotation> getAnnotation() {
        return annotation;
    }

    public void setAnnotation(List<Annotation> annotation) {
        this.annotation = annotation;
    }

    public void updateAnnotationMap(List<Annotation>  annotations) {
        Map<String,Annotation> newAnnotationMap = new HashMap<>();
        for(Annotation annotation : annotations) {

        }
    }
}