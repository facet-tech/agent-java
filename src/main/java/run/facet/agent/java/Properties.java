package run.facet.agent.java;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.stereotype.Component;
import run.facet.agent.java.exception.InstallException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

@Component
public class Properties {

    private File file;
    private ObjectMapper objectMapper;
    private final String facetYaml = "/facet.yml";

    public Properties() throws InstallException {
        this.file = getFacetYaml();
        objectMapper = new ObjectMapper(new YAMLFactory());
    }

    public String getJarPath() throws InstallException {
        try {
            return new File(Properties.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        } catch (URISyntaxException e) {
            throw new InstallException("Unable to parse facet.jar path", e);
        }
    }

    public String getFacetYamlPath() throws InstallException {
        return getJarPath() + facetYaml;
    }

    public File getFacetYaml() throws InstallException {
        File file = new File(getFacetYamlPath());
        if (!file.exists()) {
            throw new InstallException("facet.yaml not found, create file=[" + getFacetYamlPath() + "]");
        }
        return file;
    }

    public Object getProperty(Class clazz) throws InstallException {
        try {
            return objectMapper.readValue(file, clazz);
        } catch (IOException e) {
            throw new InstallException("Unable to fetch properties, file=[" + file.getAbsolutePath() + "],clazz=[" + clazz.getName() + "]");
        }

    }
}
