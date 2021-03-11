package main.java.agent;

import java.util.*;

public class Frameworks {
    private String id = "JAVA~1";
    private String property = "FRAMEWORK~";
    private Map<String, Framework> frameworks;
    private Map<String, Framework> frameworkAnnotationMap;
    private Map<String, Framework> frameworkSignatureMap;
    private Map<String, Framework> frameworkInterfaceSignatureMap;
    private static Frameworks singleton = null;


    private Frameworks() {
        fetchFrameworks();
    }

    public static Frameworks getFrameworks() {
        if (singleton == null) {
            singleton = new Frameworks();
        }
        return singleton;
    }

    private void fetchFrameworks() {
        Configuration configuration = WebRequest.fetchConfiguration(this.property, this.id);
        Map<String, Framework> frameworks = convertConfigurationToFrameworkList(new ArrayList<Configuration>(){{add(configuration);}});
        Map<String, Framework> frameworkAnnotationMap = new HashMap<>();
        Map<String, Framework> frameworkSignatureMap = new HashMap<>();
        Map<String, Framework> frameworkInterfaceSignatureMap = new HashMap<>();
        generateMaps(frameworks, frameworkAnnotationMap, frameworkSignatureMap, frameworkInterfaceSignatureMap);
        this.frameworks = frameworks;
        this.frameworkAnnotationMap = frameworkAnnotationMap;
        this.frameworkSignatureMap = frameworkSignatureMap;
        this.frameworkInterfaceSignatureMap = frameworkInterfaceSignatureMap;
    }
    public Map<String, Framework> convertConfigurationToFrameworkList(List<Configuration> configurations) {
        Map<String, Framework> frameworks = new HashMap<>();
        Map<String, Object> configurationAttribute;
        List<Annotation> annotations = null;
        for (Configuration configuration : configurations) {
            Framework framework = new Framework();
            configurationAttribute = configuration.getAttribute();
            framework.setName((String) configurationAttribute.get("name"));
            framework.setVersion((String) configuration.getAttribute().get("version"));
            annotations = new ArrayList<>();
            for (LinkedHashMap annotationAttribute : (List<LinkedHashMap>) configurationAttribute.get("annotation")) {
                Annotation annotation = new Annotation();
                annotation.setName((String) annotationAttribute.get("name"));
                Map<String,String> parameters = new HashMap<>();
                for (Map.Entry param: (Set<Map.Entry<String,String>>)((LinkedHashMap) annotationAttribute.get("parameters")).entrySet()) {
                    parameters.put((String) param.getKey(), (String) param.getValue());
                }
                annotation.setParameters(parameters);
                Breaker breaker = new Breaker();
                LinkedHashMap cb = (LinkedHashMap) annotationAttribute.get("circuitBreaker");
                breaker.setCircuitBreaker((String) cb.get("circuitBreaker"));
                breaker.setParameterMapping(new HashMap<>());

                Map<String,String> parameterMapping = new HashMap<>();
                for (Map.Entry param: (Set<Map.Entry<String,String>>)((LinkedHashMap) cb.get("parameterMapping")).entrySet()) {
                    parameterMapping.put((String) param.getKey(), (String) param.getValue());
                }
                breaker.setParameterMapping(parameterMapping);
                annotation.setCircuitBreaker(breaker);
                annotations.add(annotation);
            }
            framework.setSignature(new ArrayList<>());
            framework.setInterfaceSignature(new ArrayList<>());
            framework.setAnnotation(annotations);
            frameworks.put(framework.getName(), framework);
        }
        return frameworks;
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

