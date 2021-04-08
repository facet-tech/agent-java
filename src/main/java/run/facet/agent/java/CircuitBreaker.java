package run.facet.agent.java;

import java.util.ArrayList;
import java.util.List;

public class CircuitBreaker {
    private int precedence;
    private List<Method> methodsToCreate;
    private Toggle toggle;
    private String returnType;

    public CircuitBreaker() {
        methodsToCreate = new ArrayList<>();
    }

    public int getPrecedence() {
        return precedence;
    }

    public void setPrecedence(int precedence) {
        this.precedence = precedence;
    }

    public List<Method> getMethodsToCreate() {
        return methodsToCreate;
    }

    public void setMethodsToCreate(List<Method> methodsToCreate) {
        this.methodsToCreate = methodsToCreate;
    }

    public Toggle getToggle() {
        return toggle;
    }

    public void setToggle(Toggle toggle) {
        this.toggle = toggle;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }
}
