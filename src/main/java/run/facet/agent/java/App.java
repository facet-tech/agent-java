package run.facet.agent.java;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import run.facet.agent.java.exception.InstallException;

@Component
public class App {
    private Object attribute;
    private String name;
    private String environment;
    private String workspaceId;
    private String apiKey;
    // need this for API AppId
    private String Id;

    @Autowired
    public App(Properties properties) throws InstallException {
        App app = (App) properties.getProperty(App.class);
        this.name = app.name;
        this.environment = app.environment;
        this.workspaceId = app.workspaceId;
        this.apiKey = app.apiKey;
        if(!StringUtils.hasLength(this.name) || !StringUtils.hasLength(this.environment) || !StringUtils.hasLength(this.workspaceId)|| !StringUtils.hasLength(this.apiKey)) {
            throw new InstallException("The following properties are required in file=[" + properties.getFacetYamlPath()+ "],properties=[name, environment, workspaceId, apiKey]");
        }
    }

    public App() {
    }

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

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
