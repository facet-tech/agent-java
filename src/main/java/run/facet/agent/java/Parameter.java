package run.facet.agent.java;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Parameter {
    private String name;
    private String className;
    private Type type;
    private List<Parameter> values;
    private String value;
    private int position;

    public enum Type {
        @JsonProperty("string")
        string,
        @JsonProperty("list")
        list
    }

    public Parameter() {
        values = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<Parameter> getValues() {
        return values;
    }

    public void setValues(List<Parameter> values) {
        this.values = values;
    }

    public void addValue(Parameter value) {
        values.add(value);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
