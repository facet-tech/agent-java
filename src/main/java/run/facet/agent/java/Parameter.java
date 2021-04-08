package run.facet.agent.java;

import java.util.ArrayList;
import java.util.List;

public class Parameter {
    private String name;
    private String className;
    private String type;
    private List<Parameter> values;
    private int position;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
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
}
