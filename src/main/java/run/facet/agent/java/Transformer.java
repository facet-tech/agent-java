package run.facet.agent.java;

import javassist.*;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class Transformer implements ClassFileTransformer {
    private Properties properties;
    private App app;
    private CircuitBreaker circuitBreaker;
    private BlockList blockList;
    private Toggle toggle;
    private Facets facets;
    private Frameworks frameworks;
    private WebRequest webRequest;

    @Autowired
    public Transformer(App app, Properties properties, BlockList blockList, CircuitBreaker circuitBreaker, Toggle toggle, Facets facets, Frameworks frameworks, WebRequest webRequest) throws Exception {
        this.properties = properties;
        this.app = app;
        this.webRequest = webRequest;
        // fire web request
        webRequest.createApp((App) properties.getProperty(App.class));
        this.circuitBreaker = circuitBreaker;
        this.blockList = blockList;
        this.toggle = toggle;
        this.facets = facets;
        this.frameworks = frameworks;
    }

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            if (!blockList.contains(className)) {
                System.out.println(className);
                ClassPool classPool = ClassPool.getDefault();
                classPool.appendSystemPath();
                CtClass cf = classPool.get(className.replace("/", "."));
                facets.add(createFacet(cf, classPool));
                classfileBuffer = cf.toBytecode();
                cf.detach();
            }
        } catch (Throwable ignored) {
            System.out.println("");
            ignored.printStackTrace();
        } finally {
            return classfileBuffer;
        }
    }

    public Facet createFacet(CtClass cf, ClassPool classPool) {
        Facet facet = null;
        try {
            facet = new Facet(app.getName(), cf.getName(), cf.isInterface() ? "interface" : "class", "0.0.1", new Language("java", System.getProperty("java.version")));
            facet.setParentSignature(cf.getSuperclass().getName());
            facet.setAnnotation(parseAnnotations(cf.getAnnotations()));
            facet.setSignature(parseMethods(cf.getDeclaredMethods(), facet, classPool));
            facet.setInterfaceSignature(parseInterfaces(cf.getInterfaces()));
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return facet;
        }
    }

    public List<Signature> parseMethods(CtMethod[] methods, Facet facet, ClassPool classPool) throws CannotCompileException, NotFoundException, ClassNotFoundException {
        List<Signature> signatureList = new ArrayList<>();
        for (CtMethod method : methods) {
            if (!Modifier.isAbstract(method.getModifiers())) {
                Signature signature = new Signature(method.getName(), method.getReturnType().getName());
                addSignatureParameters(method, signature);
                signature.setEnabled(calculateEnabled(facet, signature));
                signature.setAnnotation(parseAnnotations(method.getAnnotations()));
                signatureList.add(signature);
                toggle.updateToggle(facet.getFullyQualifiedName(), signature.getSignature(), signature.isEnabled());
                insertToggleLogic(classPool, facet, method, signature);
            }
        }
        return signatureList;
    }

    public void addSignatureParameters(CtMethod method, Signature signature) throws NotFoundException {
        int position = 1;
        for (CtClass param : method.getParameterTypes()) {
            Parameter parameter = new Parameter();
            parameter.setName(param.getName());
            parameter.setType(param.getName());
            parameter.setPosition(position);
            signature.addParameter(parameter);
            position++;
        }
    }

    public boolean calculateEnabled(Facet facet, Signature signature) {
        //TODO fix race condition where facets could be overwritten by the timer during parsing and vice versa.
        boolean isEnabled = true;
        if (facets.contains(facet) && facets.get(facet.getFullyQualifiedName()).hasSignature(signature.getSignature())) {
            isEnabled = facets.get(facet.getFullyQualifiedName()).getSignature(signature.getSignature()).isEnabled();
        }
        return isEnabled;
    }

    public void insertToggleLogic(ClassPool classPool, Facet facet, CtMethod method, Signature signature) throws CannotCompileException, NotFoundException {
        Breaker breaker = getBreaker(signature);
        String toggleLogic = breaker.getCircuitBreaker().replace("${toggle}", "run.facet.agent.java.Toggle.isEnabled(\"" + toggle.getToggleName(facet.getFullyQualifiedName(), signature.getSignature()) + "\")");
        for (Map.Entry<String, String> entry : breaker.getParameterMapping().entrySet()) {
            if (signature.getParameterByReturnType(entry.getValue()) == null) {
                method.insertParameter(classPool.get(entry.getValue()));
                toggleLogic = toggleLogic.replace("${" + entry.getKey() + "}", "$1");
            } else {
                toggleLogic = toggleLogic.replace("${" + entry.getKey() + "}", "$" + signature.getParameterByReturnType(entry.getValue()).getPosition());
            }
        }
        method.insertBefore(toggleLogic);
    }

    public Breaker getBreaker(Signature signature) throws CannotCompileException, NotFoundException {
        Breaker breaker;
        if (frameworks.isFramework(signature.getAnnotation())) {
            breaker = frameworks.getFramework(signature.getAnnotation()).getAnnotation().get(0).getCircuitBreaker();
        } else {
            breaker = circuitBreaker.getBreaker(signature.getReturnType());
        }
        return breaker;
    }

    public List<run.facet.agent.java.Annotation> parseAnnotations(Object[] objectList) {
        List<run.facet.agent.java.Annotation> annotations = new ArrayList<>();
        for (Object object : objectList) {
            if (object instanceof Proxy) {
                InvocationHandler invocationHandler = Proxy.getInvocationHandler((Proxy) object);
                if (invocationHandler instanceof AnnotationImpl) {
                    Annotation annotation = ((AnnotationImpl) invocationHandler).getAnnotation();
                    run.facet.agent.java.Annotation facetAnnotation = new run.facet.agent.java.Annotation();
                    facetAnnotation.setName(annotation.getTypeName());
                    Set<String> parameters = annotation.getMemberNames();
                    if (parameters != null) {
                        for (String parameter : parameters) {
                            facetAnnotation.addParameter(parameter, annotation.getMemberValue(parameter).toString());
                        }
                    }
                    annotations.add(facetAnnotation);
                }
            }
        }
        return annotations;
    }

    public List<String> parseInterfaces(CtClass[] interfaces) {
        List<String> interfaceList = new ArrayList<>();
        for (CtClass inter : interfaces) {
            interfaceList.add(inter.getName());
        }
        return interfaceList;
    }
}