package run.facet.agent.java;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

@Component
public class Properties {

    private File file;
    private ObjectMapper objectMapper;
    private final String facetYaml = "/facet.yml";

    public Properties() throws URISyntaxException {
        this.file = getFacetYaml();
        objectMapper = new ObjectMapper(new YAMLFactory());
    }

    public String getJarPath() throws URISyntaxException {
        return new File(Properties.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
    }

    public File getFacetYaml() throws URISyntaxException {
        return new File(getJarPath() + facetYaml);
    }

    public Object getProperty(Class clazz) throws IOException {
        return objectMapper.readValue(file, clazz);
    }
}
