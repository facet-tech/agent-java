package run.facet.agent.java;

import javassist.CtClass;
import javassist.Modifier;

import java.util.ArrayList;
import java.util.List;

public class Method {
    private List<Annotation> annotations;
    private String body;
    private List<Exception> exceptions;
    private String modifier;
    private String name;
    private List<Parameter> parameters;
    private String returnType;

    public Method() {
        this.annotations = new ArrayList<>();
        this.exceptions = new ArrayList<>();
        this.parameters = new ArrayList<>();
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<Exception> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<Exception> exceptions) {
        this.exceptions = exceptions;
    }

    public String getModifier() {
        return modifier;
    }

    public int getModifierInt (String modifier) {
        int modifierInt;
        switch(modifier) {
            case "run.facet.dependencies.javassist.bytecode.AccessFlag.Modifier.PUBLIC":
                modifierInt = Modifier.PUBLIC;
                break;
            case "run.facet.dependencies.javassist.bytecode.AccessFlag.Modifier.PRIVATE":
                modifierInt = Modifier.PRIVATE;
                break;
            case "run.facet.dependencies.javassist.bytecode.AccessFlag.Modifier.PROTECTED":
                modifierInt = Modifier.PROTECTED;
                break;
            case "run.facet.dependencies.javassist.bytecode.AccessFlag.Modifier.STATIC":
                modifierInt = Modifier.STATIC;
                break;
            case "run.facet.dependencies.javassist.bytecode.AccessFlag.Modifier.FINAL":
                modifierInt = Modifier.FINAL;
                break;
            case "run.facet.dependencies.javassist.bytecode.AccessFlag.Modifier.SYNCHRONIZED":
                modifierInt = Modifier.SYNCHRONIZED;
                break;
            case "run.facet.dependencies.javassist.bytecode.AccessFlag.Modifier.VOLATILE":
                modifierInt = Modifier.VOLATILE;
                break;
            case "run.facet.dependencies.javassist.bytecode.AccessFlag.Modifier.VARARGS":
                modifierInt = Modifier.VARARGS;
                break;
            case "run.facet.dependencies.javassist.bytecode.AccessFlag.Modifier.TRANSIENT":
                modifierInt = Modifier.TRANSIENT;
                break;
            case "run.facet.dependencies.javassist.bytecode.AccessFlag.Modifier.NATIVE":
                modifierInt = Modifier.NATIVE;
                break;
            case "run.facet.dependencies.javassist.bytecode.AccessFlag.Modifier.INTERFACE":
                modifierInt = Modifier.INTERFACE;
                break;
            case "run.facet.dependencies.javassist.bytecode.AccessFlag.Modifier.ABSTRACT":
                modifierInt = Modifier.ABSTRACT;
                break;
            case "run.facet.dependencies.javassist.bytecode.AccessFlag.Modifier.STRICT":
                modifierInt = Modifier.STRICT;
                break;
            case "run.facet.dependencies.javassist.bytecode.AccessFlag.Modifier.ANNOTATION":
                modifierInt = Modifier.ANNOTATION;
                break;
            case "run.facet.dependencies.javassist.bytecode.AccessFlag.Modifier.ENUM":
                modifierInt = Modifier.ENUM;
                break;
            default:
                modifierInt = Integer.MIN_VALUE;
        }
        return modifierInt;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public String getReturnType() {
        return this.returnType;
    }

    public CtClass getReturnType2(String returnType) {
        CtClass ctClass;
        switch (returnType) {
            case "run.facet.dependencies.javassist.CtClass.booleanType":
                ctClass = CtClass.booleanType;
                break;
            case "run.facet.dependencies.javassist.CtClass.charType":
                ctClass = CtClass.charType;
                break;
            case "run.facet.dependencies.javassist.CtClass.byteType":
                ctClass = CtClass.byteType;
                break;
            case "run.facet.dependencies.javassist.CtClass.shortType":
                ctClass = CtClass.shortType;
                break;
            case "run.facet.dependencies.javassist.CtClass.intType":
                ctClass = CtClass.intType;
                break;
            case "run.facet.dependencies.javassist.CtClass.longType":
                ctClass = CtClass.longType;
                break;
            case "run.facet.dependencies.javassist.CtClass.floatType":
                ctClass = CtClass.floatType;
                break;
            case "run.facet.dependencies.javassist.CtClass.doubleType":
                ctClass = CtClass.doubleType;
                break;
            case "run.facet.dependencies.javassist.CtClass.voidType":
                ctClass = CtClass.voidType;
                break;
            default:
                ctClass = null;
        }
        return ctClass;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }
}

