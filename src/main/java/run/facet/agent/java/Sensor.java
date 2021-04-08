package run.facet.agent.java;

import java.util.ArrayList;
import java.util.List;

public class Sensor {
    private List<Annotation> annotations;
    private String returnType;

    public Sensor() {
        annotations = new ArrayList<>();
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }
}
