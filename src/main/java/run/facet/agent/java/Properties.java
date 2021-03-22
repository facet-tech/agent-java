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

    public Properties() throws URISyntaxException {
        this.file = getJarPath();
        objectMapper = new ObjectMapper(new YAMLFactory());
    }

    private File getJarPath() throws URISyntaxException {
        return new File(new File(Properties.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent() + "/facet.yml");
    }

    public Object getProperty(Class clazz) throws IOException {
        return objectMapper.readValue(file, clazz);
    }
}
