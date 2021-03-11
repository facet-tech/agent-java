package run.facet.agent.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Facet {
    private String appId;
    private String fullyQualifiedName;
    private List<Signature> signature;
    private Map<String, Signature> signatureMap;
    private String version;
    private Map<String, Object> attribute;
    private Language language;
    private List<Framework> framework;
    private List<String> interfaceSignature;
    private String parentSignature;
    private String type;
    private List<Annotation> annotation;


    public Facet(String appId, String fullyQualifiedName, String type, String version, Language language) {
        this.appId = appId;
        this.fullyQualifiedName = fullyQualifiedName;
        this.version = version;
        this.language = language;
        this.type = type;
    }

    public Facet() {
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public void setFullyQualifiedName(String fullyQualifiedName) {
        this.fullyQualifiedName = fullyQualifiedName;
    }

    public List<Signature> getSignature() {
        return signature;
    }

    public void setSignature(List<Signature> signatureList) {
        if(signatureList == null) {
            signatureList = new ArrayList<>();
        }
        this.signature = signatureList;
        signatureMap = new HashMap<>();
        for (Signature signature : signatureList) {
            signatureMap.put(signature.getSignature(), signature);
        }
    }

    public boolean hasSignature(String signature) {
        return !(signatureMap == null) && signatureMap.containsKey(signature);
    }

    public Signature getSignature(String signature) {
        return signatureMap.get(signature);
    }

    public String getParentSignature() {
        return parentSignature;
    }

    public void setParentSignature(String parentSignature) {
        this.parentSignature = parentSignature;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, Object> getAttribute() {
        return attribute;
    }

    public void setAttribute(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public List<Framework> getFramework() {
        return framework;
    }

    public void setFramework(List<Framework> framework) {
        this.framework = framework;
    }

    public List<String> getInterfaceSignature() {
        return interfaceSignature;
    }

    public void setInterfaceSignature(List<String> interfaceSignature) {
        this.interfaceSignature = interfaceSignature;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Annotation> getAnnotation() {
        return annotation;
    }

    public void setAnnotation(List<Annotation> annotation) {
        this.annotation = annotation;
    }
}