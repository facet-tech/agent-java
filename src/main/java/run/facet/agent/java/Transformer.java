package run.facet.agent.java;

import javassist.*;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationImpl;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Transformer implements ClassFileTransformer {

    private AgentBiz agentBiz;

    public void setAgentBiz(AgentBiz agentBiz) {
        this.agentBiz = agentBiz;
    }

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {

        if (!agentBiz.getBlockList().contains(className)) {
            try {
                System.out.println(className);
                ClassPool classPool = ClassPool.getDefault();
                CtClass cf = null;
                classPool.appendSystemPath();
                cf = classPool.get(className.replace("/", "."));
                Facet facet = createFacet(cf, classPool);
                classfileBuffer = cf.toBytecode();
                cf.detach();
                return instrument(classfileBuffer, loader);
            } catch (Throwable ignored) { //
                System.err.println(ignored);
            } finally {

                return classfileBuffer;
            }
        } else {
            return classfileBuffer;
        }
    }

    public Facet createFacet(CtClass cf, ClassPool classPool) {
        Facet facet = null;
        try {
            facet = new Facet(agentBiz.getApp().getName(), cf.getName(), cf.isInterface() ? "interface" : "class", "0.0.1", new Language("java", System.getProperty("java.version")));
            facet.setParentSignature(cf.getSuperclass().getName());
            List<Signature> signatureList = new ArrayList<>();
            facet.setAnnotation(parseAnnotations(cf.getAnnotations()));
            for (CtMethod method : cf.getDeclaredMethods()) {
                if (!Modifier.isAbstract(method.getModifiers())) {
                    Signature signature = new Signature(method.getName(), method.getReturnType().getName());
                    int position = 1;
                    for (CtClass param : method.getParameterTypes()) {
                        Parameter parameter = new Parameter();
                        parameter.setName(param.getName());
                        parameter.setType(param.getName());
                        parameter.setPosition(position);
                        signature.addParameter(parameter);
                        position++;
                    }

                    if (agentBiz.getFacets().contains(facet) && agentBiz.getFacets().get(facet.getFullyQualifiedName()).hasSignature(signature.getSignature())) {
                        signature.setEnabled(agentBiz.getFacets().get(facet.getFullyQualifiedName()).getSignature(signature.getSignature()).isEnabled());
                    } else {
                        signature.setEnabled(true);
                    }
                    agentBiz.getToggle().updateToggle(facet.getFullyQualifiedName(), signature.getSignature(), signature.isEnabled());
                    signature.setAnnotation(parseAnnotations(method.getAnnotations()));
                    signatureList.add(signature);
                    Breaker breaker;
                    if (agentBiz.getFrameworks().isFramework(signature.getAnnotation())) {
                        breaker = agentBiz.getFrameworks().getFramework(signature.getAnnotation()).getAnnotation().get(0).getCircuitBreaker();
                    } else {
                        breaker = agentBiz.getCircuitBreaker().getBreaker(signature.getReturnType());
                    }
                    String toggleLogic = breaker.getCircuitBreaker().replace("${toggle}", "run.facet.agent.java.Toggle.isEnabled(\"" + agentBiz.getToggle().getToggleName(facet.getFullyQualifiedName(), signature.getSignature()) + "\")");
                    for (Map.Entry<String, String> entry : breaker.getParameterMapping().entrySet()) {
                        if(signature.getParameterByReturnType(entry.getValue()) == null) {
                            method.insertParameter(classPool.get(entry.getValue()));
                            toggleLogic = toggleLogic.replace("${" + entry.getKey() + "}", "$1");
                        } else {
                            toggleLogic = toggleLogic.replace("${" + entry.getKey() + "}", "$" + signature.getParameterByReturnType(entry.getValue()).getPosition());
                        }

                    }
                    System.out.println(toggleLogic);
                    method.insertBefore(toggleLogic);

                }
            }
            facet.setSignature(signatureList);
            List<String> interfaceList = new ArrayList<>();
            for (CtClass inter : cf.getInterfaces()) {
                interfaceList.add(inter.getName());
            }
            facet.setInterfaceSignature(interfaceList);
            agentBiz.getFacets().add(facet);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return facet;
        }
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

    public byte[] instrument(byte[] origBytes, ClassLoader loader) {
        return new byte[15];
    }
}