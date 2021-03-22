package run.facet.agent.java;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Frameworks {
    private String id = "JAVA~1";
    private String property = "FRAMEWORK~";
    private Map<String, Framework> frameworks;
    private Map<String, Framework> frameworkAnnotationMap;
    private Map<String, Framework> frameworkSignatureMap;
    private Map<String, Framework> frameworkInterfaceSignatureMap;

    private WebRequest webRequest;

    @Autowired
    private Frameworks(WebRequest webRequest) {
        this.webRequest = webRequest;
        fetchFrameworks();
    }

    private void fetchFrameworks() {
        Framework framework = (Framework) webRequest.fetchConfiguration(this.property, this.id,"attribute", Framework.class);
        Map<String, Framework> frameworks = new HashMap<>(){{put(framework.getName(),framework);}};
        Map<String, Framework> frameworkAnnotationMap = new HashMap<>();
        Map<String, Framework> frameworkSignatureMap = new HashMap<>();
        Map<String, Framework> frameworkInterfaceSignatureMap = new HashMap<>();
        generateMaps(frameworks, frameworkAnnotationMap, frameworkSignatureMap, frameworkInterfaceSignatureMap);
        this.frameworks = frameworks;
        this.frameworkAnnotationMap = frameworkAnnotationMap;
        this.frameworkSignatureMap = frameworkSignatureMap;
        this.frameworkInterfaceSignatureMap = frameworkInterfaceSignatureMap;
    }
    
    public void generateMaps(Map<String, Framework> frameworks, Map<String, Framework> frameworkAnnotationMap, Map<String, Framework> frameworkSignatureMap, Map<String, Framework> frameworkInterfaceSignatureMap) {
        for (Framework framework : frameworks.values()) {
            for (Signature signature : framework.getSignature()) {
                frameworkSignatureMap.put(signature.getSignature(), framework);
            }
            for (Signature signature : framework.getInterfaceSignature()) {
                frameworkInterfaceSignatureMap.put(signature.getSignature(), framework);
            }
            for (Annotation annotation : framework.getAnnotation()) {
                frameworkAnnotationMap.put(annotation.getName(), framework);
            }
        }
    }

    public boolean isFramework(Annotation annotation) {
        return frameworkAnnotationMap.containsKey(annotation.getName());
    }

    public boolean isFramework(List<Annotation> annotations) {
        for(Annotation annotation : annotations) {
            if(isFramework(annotation)) {
                return true;
            }
        }
        return false;
    }

    public Framework getFramework(Annotation annotation) {
        return frameworkAnnotationMap.get(annotation.getName());
    }

    public Framework getFramework(List<Annotation> annotations) {
        for(Annotation annotation : annotations) {
            if(isFramework(annotation)) {
                return getFramework(annotation);
            }
        }
        return null;
    }
}

