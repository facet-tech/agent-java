package run.facet.agent.java;

import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.AnnotationImpl;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.MemberValue;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.ProtectionDomain;
import java.util.*;

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
    private LogInitializer logInitializer;
    private Logger logger;

    @Autowired
    public Transformer(App app, Properties properties, BlockList blockList, CircuitBreakers circuitBreakers, Toggles toggles, Facets facets, Frameworks frameworks, WebRequest webRequest, LogInitializer logInitializer) throws java.lang.Exception {
        this.properties = properties;
        this.logInitializer = logInitializer;
        this.logger = logInitializer.getLogger();
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
                logger.info(className);
                ClassPool classPool = ClassPool.getDefault();
                classPool.appendSystemPath();
                try {
                    CtClass cf = classPool.get(className.replace("/", "."));
                    facets.add(createFacet(cf, classPool));
                    classfileBuffer = cf.toBytecode();
                    writeModifiedClassToFile(className, classfileBuffer);
                    cf.detach();
                } catch (NotFoundException e) {
                    logger.warn("Unable to find class:" + className);
                }
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(),e);
        } finally {
            return classfileBuffer;
        }
    }

    public void writeModifiedClassToFile(String className, byte[] classToWrite) {
        FileOutputStream fos = null;
        try {
            String baseDir = properties.getJarPath() + "/facetRunModifiedClasses/";
            Files.createDirectories(Path.of(baseDir));
            File test = new File(baseDir + className.replace("/", ".") + ".class");
            fos = new FileOutputStream(test);
            fos.write(classToWrite);
        } catch (IOException e) {
            logger.error(e.getStackTrace());
        } catch (URISyntaxException e) {
            logger.error(e.getStackTrace());
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public Facet createFacet(CtClass cf, ClassPool classPool) throws NotFoundException, ClassNotFoundException, CannotCompileException {
        Facet facet = null;
        facet = new Facet(app.getName(), cf.getName(), cf.isInterface() ? "interface" : "class", "0.0.1", new Language("java", System.getProperty("java.version")));
        facet.setParentSignature(cf.getSuperclass().getName());
        facet.setAnnotation(parseAnnotations(cf.getAnnotations()));
        facet.setSignature(parseMethods(cf, cf.getDeclaredMethods(), facet, classPool));
        facet.setInterfaceSignature(parseInterfaces(cf.getInterfaces()));
        return facet;

    }

    public List<Signature> parseMethods(CtClass ctClass, CtMethod[] methods, Facet facet, ClassPool classPool) throws CannotCompileException, NotFoundException, ClassNotFoundException {
        List<Signature> signatureList = new ArrayList<>();
        HashMap<String, String> createdMethods = new HashMap<>();
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
                insertToggleLogic(ctClass, classPool, facet, method, signature, createdMethods);
            }
        }
        return signatureList;
    }

    public void addSignatureParameters(CtMethod method, Signature signature) throws NotFoundException {
        int position = 1;
        for (CtClass param : method.getParameterTypes()) {
            Parameter parameter = new Parameter();
            parameter.setClassName(param.getName());
            parameter.setPosition(position);
            parameter.setType(Parameter.Type.string);
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

    public void insertToggleLogic(CtClass ctclass, ClassPool classPool, Facet facet, CtMethod method, Signature signature, Map<String, String> createdMethods) throws CannotCompileException, NotFoundException {
        CircuitBreaker circuitBreaker = getBreaker(signature);
        List<CtMethod> methodsToCreate = createMethods(circuitBreaker, ctclass, classPool, method, signature, facet, createdMethods);

        for (CtMethod ctMethod : methodsToCreate) {
            ctclass.addMethod(ctMethod);
        }
        if (circuitBreaker.getToggle().getMethod().getExceptions().size() > 0) {
            method.setExceptionTypes(createExceptions(circuitBreaker.getToggle().getMethod().getExceptions(), method.getExceptionTypes(), classPool));
        }
        method.insertBefore(createToggleLogic(signature, circuitBreaker.getToggle(), facet));
    }

    public List<CtMethod> createMethods(CircuitBreaker circuitBreaker, CtClass ctclass, ClassPool classPool, CtMethod method, Signature signature, Facet facet, Map<String, String> createdMethods) throws CannotCompileException, NotFoundException {
        List<CtMethod> methods = new ArrayList<>();
        for (Method methodToCreate : circuitBreaker.getMethodsToCreate()) {
            if (!createdMethods.containsKey(methodToCreate.getName())) {
                createdMethods.put(methodToCreate.getName(), methodToCreate.getName());
                ConstPool constantPool = method.getMethodInfo().getConstPool();

                CtMethod ctMethod = CtNewMethod.make(
                        methodToCreate.getModifierInt(methodToCreate.getModifier()),
                        methodToCreate.getReturnType2(methodToCreate.getReturnType()),
                        methodToCreate.getName(),
                        getParameters(methodToCreate, classPool),
                        getExceptions(methodToCreate, classPool),
                        createToggleLogic(signature, new Toggle(methodToCreate), facet) /* replace body */,
                        ctclass
                );

                for (Annotation annotation : methodToCreate.getAnnotations()) {
                    AnnotationsAttribute attr = new AnnotationsAttribute(constantPool, annotation.getVisibilityString(annotation.getVisibility()));
                    javassist.bytecode.annotation.Annotation ann = new javassist.bytecode.annotation.Annotation(annotation.getClassName(), constantPool);
                    for (Parameter parameter : annotation.getParameters()) {
                        if ("run.facet.dependencies.javassist.bytecode.annotation.ArrayMemberValue".equals(parameter.getClassName())) {
                            ArrayMemberValue arrayMemberValue = new ArrayMemberValue(constantPool);
                            MemberValue[] memberValueArray = new MemberValue[parameter.getValues().size()];
                            int index = 0;
                            for (Parameter subParam : parameter.getValues()) {
                                if ("run.facet.dependencies.javassist.bytecode.annotation.ClassMemberValue".equals(subParam.getClassName())) {
                                    ClassMemberValue classMemberValue = new ClassMemberValue(subParam.getValue(), constantPool);
                                    memberValueArray[index] = classMemberValue;
                                    index++;
                                }
                            }
                            arrayMemberValue.setValue(memberValueArray);
                            ann.addMemberValue(parameter.getName(), arrayMemberValue);
                            attr.addAnnotation(ann);
                            ctMethod.getMethodInfo().addAttribute(attr);
                        }
                    }
                }
                methods.add(ctMethod);
            }
        }
        return methods;
    }

    public String createToggleLogic(Signature signature, Toggle toggle, Facet facet) {
        String toggleLogic = toggle.getMethod().getBody().replace("${toggle}", "run.facet.agent.java.Toggles.isEnabled(\"" + toggles.getToggleName(facet.getFullyQualifiedName(), signature.getSignature()) + "\")");
        for (Map.Entry<String, String> entry : toggle.getParameterMapping().entrySet()) {
            toggleLogic = toggleLogic.replace("${" + entry.getKey() + "}", "$" + signature.getParameterByReturnType(entry.getValue()).getPosition());
        }
        return toggleLogic;
    }

    public CtClass[] createExceptions(List<Exception> exceptions, CtClass[] existingExceptions, ClassPool classPool) throws NotFoundException {
        CtClass[] exceptionList = new CtClass[exceptions.size() + existingExceptions.length];
        int index = 0;
        for (CtClass exception : existingExceptions) {
            exceptionList[index] = exception;
            index++;
        }
        for (Exception exception : exceptions) {
            exceptionList[index] = classPool.get(exception.getClassName());
        }
        return exceptionList;
    }

    public CtClass[] getParameters(Method method, ClassPool classPool) throws NotFoundException {
        CtClass[] parameters = new CtClass[method.getParameters().size()];
        int index = 0;
        for (Parameter parameter : method.getParameters()) {
            parameters[index] = classPool.get(parameter.getClassName());
            index++;
        }
        return parameters;
    }

    public CtClass[] getExceptions(Method method, ClassPool classPool) throws NotFoundException {
        CtClass[] exceptions = new CtClass[method.getExceptions().size()];
        int index = 0;
        for (Exception exception : method.getExceptions()) {
            exceptions[index] = classPool.get(exception.getClassName());
            index++;
        }
        return exceptions;
    }


    public CircuitBreaker getBreaker(Signature signature) {
        CircuitBreaker circuitBreaker = null;
        if (frameworks.isFramework(signature.getAnnotation())) {
            List<CircuitBreaker> cbs = frameworks.getFramework(signature.getAnnotation()).getCircuitBreakers();
            outerLoop:
            for (CircuitBreaker cb : cbs) {
                for (Map.Entry<String, String> entry : cb.getToggle().getParameterMapping().entrySet()) {
                    if (signature.getParameterByReturnType(entry.getValue()) == null) {
                        continue outerLoop;
                    }
                }
                circuitBreaker = cb;
                break;
            }
        }
        if (circuitBreaker == null) {
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
                            param.setType(Parameter.Type.string);
                            param.setName(parameter);
                            param.setValue(annotation.getMemberValue(parameter).toString());
                            facetAnnotation.addParameter(param);
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