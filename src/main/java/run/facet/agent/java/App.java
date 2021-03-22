package run.facet.agent.java;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class App {
    private Object attribute;
    private String name;
    private String id;
    private String environment;
    private String workspaceId;

    @Autowired
    public App(WebRequest webRequest, Properties properties) throws IOException {
        App app = webRequest.createApp((App) properties.getProperty(App.class));
        this.name = app.name;
        this.id = app.id;
        this.environment = app.environment;
        this.workspaceId = app.workspaceId;
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
