package run.facet.agent.java;

public class App {
    private Object attribute;
    private String name;
    private String id;
    private String environment;
    private String workspaceId;

    public App(String workspaceId, String name, String environment) {
        this.name = name;
        this.environment = environment;
        this.workspaceId = workspaceId;
    }

    public App(){}

    public Object getAttribute() {
        return attribute;
    }

    public void setAttribute(Object attribute) {
        this.attribute = attribute;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }
}
