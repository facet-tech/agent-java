package main.java.agent;

import java.util.List;

public class Framework {
    private String name;
    private String version;
    private List<Annotation> annotation;
    private List<Signature> interfaceSignature;
    private List<Signature> signature;

    public Framework() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Annotation> getAnnotation() {
        return annotation;
    }

    public void setAnnotation(List<Annotation> annotation) {
        this.annotation = annotation;
    }

    public List<Signature> getInterfaceSignature() {
        return interfaceSignature;
    }

    public void setInterfaceSignature(List<Signature> interfaceSignature) {
        this.interfaceSignature = interfaceSignature;
    }

    public List<Signature> getSignature() {
        return signature;
    }

    public void setSignature(List<Signature> signature) {
        this.signature = signature;
    }
}

