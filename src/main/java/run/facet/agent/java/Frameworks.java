package run.facet.agent.java;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Frameworks {
    private String id = "JAVA~1";
    private String property = "FRAMEWORK~";
    private List<Framework> frameworks;
    private Map<String, Framework> frameworkAnnotationMap;

    private WebRequest webRequest;

    @Autowired
    private Frameworks(WebRequest webRequest) {
        this.webRequest = webRequest;
        fetchFrameworks();
    }

    private void fetchFrameworks() {
        Framework framework = (Framework) webRequest.fetchConfiguration(this.property, this.id,"attribute", Framework.class);
        List<Framework> frameworks = new ArrayList<>(){{add(framework);}};
        this.frameworks = frameworks;
        this.frameworkAnnotationMap = generateMap(frameworks);
    }
    
    public Map<String, Framework>  generateMap(List<Framework> frameworks) {
        Map<String, Framework> frameworkAnnotationMap = new HashMap<>();
        for (Framework framework : frameworks) {
            for (Sensor sensor : framework.getSensors()) {
                for(Annotation annotation : sensor.getAnnotations())
                frameworkAnnotationMap.put(annotation.getClassName(), framework);
            }
        }
        return frameworkAnnotationMap;
    }

    public boolean isFramework(Annotation annotation) {
        return frameworkAnnotationMap.containsKey(annotation.getClassName());
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
        return frameworkAnnotationMap.get(annotation.getClassName());
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