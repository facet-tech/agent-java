package run.facet.agent.java;

import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.AnnotationImpl;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
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
    private CircuitBreakers circuitBreakers;
    private BlockList blockList;
    private Toggles toggles;
    private Facets facets;
    private Frameworks frameworks;
    private WebRequest webRequest;

    @Autowired
    public Transformer(App app, Properties properties, BlockList blockList, CircuitBreakers circuitBreakers, Toggles toggles, Facets facets, Frameworks frameworks, WebRequest webRequest) throws java.lang.Exception {
        this.properties = properties;
        this.app = app;
        this.webRequest = webRequest;
        webRequest.createApp((App) properties.getProperty(App.class));
        this.circuitBreakers = circuitBreakers;
        this.blockList = blockList;
        this.toggles = toggles;
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
                File test = new File("test/" + className.replace("/", ".") + ".class");
                FileOutputStream fos = new FileOutputStream(test);
                fos.write(classfileBuffer);
                fos.close();
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
            facet.setSignature(parseMethods(cf, cf.getDeclaredMethods(), facet, classPool));
            facet.setInterfaceSignature(parseInterfaces(cf.getInterfaces()));
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        } finally {
            return facet;
        }
    }

    public List<Signature> parseMethods(CtClass ctClass, CtMethod[] methods, Facet facet, ClassPool classPool) throws CannotCompileException, NotFoundException, ClassNotFoundException {
        List<Signature> signatureList = new ArrayList<>();
        for (CtMethod method : methods) {
            if (!Modifier.isAbstract(method.getModifiers())) {
                Signature signature = new Signature();
                signature.setName(method.getName());
                signature.setReturnType(method.getReturnType().getName());
                addSignatureParameters(method, signature);
                signature.setEnabled(calculateEnabled(facet, signature));
                signature.setAnnotation(parseAnnotations(method.getAnnotations()));
                signatureList.add(signature);
                toggles.updateToggle(facet.getFullyQualifiedName(), signature.getSignature(), signature.isEnabled());
                insertToggleLogic(ctClass, classPool, facet, method, signature);
            }
        }
        return signatureList;
    }

    public void addSignatureParameters(CtMethod method, Signature signature) throws NotFoundException {
        int position = 1;
        for (CtClass param : method.getParameterTypes()) {
            Parameter parameter = new Parameter();
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

    public void insertToggleLogic(CtClass ctclass, ClassPool classPool, Facet facet, CtMethod method, Signature signature) throws CannotCompileException, NotFoundException {
        CircuitBreaker circuitBreaker = getBreaker(signature);
        String toggleLogic = circuitBreaker.getToggle().getMethod().getBody().replace("${toggle}", "run.facet.agent.java.Toggles.isEnabled(\"" + toggles.getToggleName(facet.getFullyQualifiedName(), signature.getSignature()) + "\")");
        for (Map.Entry<String, String> entry : circuitBreaker.getToggle().getParameterMapping().entrySet()) {
            if (signature.getParameterByReturnType(entry.getValue()) == null) {


                //Create exceptionHandler
                CtClass request = classPool.get("javax.servlet.http.HttpServletRequest");
                CtClass response = classPool.get("javax.servlet.http.HttpServletResponse");
                CtClass circuitBreakerException = classPool.get(CircuitBreakerException.class.getName());

                CtMethod ctMethod = CtNewMethod.make(Modifier.PUBLIC, CtClass.voidType, "handleFacetRunCircuitBreakerException", new CtClass[]{circuitBreakerException,request,response}, null, "try { $3.sendError(403,\"Access Denied\");  } catch (Exception e) { }", ctclass);

                ConstPool dude = method.getMethodInfo().getConstPool();
                String exceptionHandlerString = "org.".concat("springframework.web.bind.annotation.ExceptionHandler");
                CtClass c1 = classPool.get(exceptionHandlerString);

                AnnotationsAttribute attr = new AnnotationsAttribute(dude, AnnotationsAttribute.visibleTag);
                javassist.bytecode.annotation.Annotation annotation = new javassist.bytecode.annotation.Annotation(exceptionHandlerString, dude);

                ClassMemberValue classMemberValue = new ClassMemberValue(circuitBreakerException.getName(), dude);
                ArrayMemberValue arrayMemberValue = new ArrayMemberValue(dude);

                ClassMemberValue[] classArray = {classMemberValue};
                arrayMemberValue.setValue(classArray);
                annotation.addMemberValue("value",arrayMemberValue);
                attr.addAnnotation(annotation);
                ctMethod.getMethodInfo().addAttribute(attr);
                ctclass.addMethod(ctMethod);


                //Throw exception
                CtClass[] exceptionList = new CtClass[method.getExceptionTypes().length + 1];
                int index = 0;
                for (CtClass exception : method.getExceptionTypes()) {
                    exceptionList[index] = exception;
                    index++;
                }
                exceptionList[index] = circuitBreakerException;
                method.setExceptionTypes(exceptionList);
                toggleLogic = "if(!run.facet.agent.java.Toggles.isEnabled(\"" + toggles.getToggleName(facet.getFullyQualifiedName(), signature.getSignature()) + "\")) {throw new run.facet.agent.java.CircuitBreakerException();}";
            } else {
                toggleLogic = toggleLogic.replace("${" + entry.getKey() + "}", "$" + signature.getParameterByReturnType(entry.getValue()).getPosition());
            }
        }
        method.insertBefore(toggleLogic);
    }

    public CircuitBreaker getBreaker(Signature signature) throws CannotCompileException, NotFoundException {
        CircuitBreaker circuitBreaker;
        if (frameworks.isFramework(signature.getAnnotation())) {
            circuitBreaker = frameworks.getFramework(signature.getAnnotation()).getCircuitBreakers().get(0);
        } else {
            circuitBreaker = circuitBreakers.getBreaker(signature.getReturnType());
        }
        return circuitBreaker;
    }

    public List<run.facet.agent.java.Annotation> parseAnnotations(Object[] objectList) {
        List<run.facet.agent.java.Annotation> annotations = new ArrayList<>();
        for (Object object : objectList) {
            if (object instanceof Proxy) {
                InvocationHandler invocationHandler = Proxy.getInvocationHandler((Proxy) object);
                if (invocationHandler instanceof AnnotationImpl) {
                    javassist.bytecode.annotation.Annotation annotation = ((AnnotationImpl) invocationHandler).getAnnotation();
                    run.facet.agent.java.Annotation facetAnnotation = new run.facet.agent.java.Annotation();
                    facetAnnotation.setClassName(annotation.getTypeName());
                    Set<String> parameters = annotation.getMemberNames();
                    if (parameters != null) {
                        for (String parameter : parameters) {
                            Parameter param = new Parameter();
                            Parameter value = new Parameter();
                            param.setName(parameter);
                            value.setClassName(annotation.getMemberValue(parameter).toString());
                            facetAnnotation.addParameter(param);
                        }
                    }
                    ///annotation.getTypeName()
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