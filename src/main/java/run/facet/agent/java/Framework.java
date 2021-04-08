package run.facet.agent.java;

import java.util.ArrayList;
import java.util.List;

public class Framework {
    private String name;
    private String version;
    private List<CircuitBreaker> circuitBreakers;
    private List<Sensor> sensors;

    public Framework() {
        this.circuitBreakers = new ArrayList<>();
        this.sensors = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<CircuitBreaker> getCircuitBreakers() {
        return circuitBreakers;
    }

    public void setCircuitBreakers(List<CircuitBreaker> circuitBreakers) {
        this.circuitBreakers = circuitBreakers;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }
}

