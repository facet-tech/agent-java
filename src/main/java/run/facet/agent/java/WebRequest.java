package run.facet.agent.java;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import run.facet.agent.java.exception.InstallException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;

@Component
public class WebRequest {

    private String API_KEY = "ApiKey";
    private static ExecutorService threadPool = Executors.newFixedThreadPool(20, new MyThreadFactory());
    private final String BaseUrl = "https://api.facet.run/";
    private App app;
    private static Logger logger;
    private LogInitializer logInitializer;
    private Properties properties;

    @Autowired
    public WebRequest(App app, LogInitializer logInitializer, Properties properties) throws InstallException {
        this.logInitializer = logInitializer;
        this.logger = logInitializer.getLogger();
        this.app = app;
        this.properties = properties;
        createApp();
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.debug("Performing some shutdown cleanup...");
            threadPool.shutdown();
            while (true) {
                try {
                    logger.debug("Waiting for the service to terminate...");
                    if (threadPool.awaitTermination(30, TimeUnit.SECONDS)) {
                        break;
                    }
                } catch (InterruptedException e) {
                }
            }
            logger.debug("Done cleaning");
        }));
    }

    public void handleAuthenticationError(HttpResponse<String> response) throws InstallException {
        String facetYamlPath = "";
        try {facetYamlPath = properties.getFacetYamlPath();} catch (java.lang.Exception e){}
        if(response.statusCode() == 401) {
            throw new InstallException("Authentication error, verify your apiKey is correctly set in facet.yaml=[" + facetYamlPath + "]");
        }

    }

    public List<Facet> fetchFacet() {
        List<Facet> facetList = null;
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(BaseUrl + "facet/backend?appId=" + app.getName()))
                    .version(HttpClient.Version.HTTP_1_1)
                    .header(API_KEY, app.getApiKey())
                    .GET()
                    .timeout(Duration.ofMillis(10000))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            handleAuthenticationError(response);
            ObjectMapper objectMapper = new ObjectMapper();
            facetList = objectMapper.readValue(response.body(), new TypeReference<List<Facet>>() {});
        } catch (Throwable e) {
            logger.error(e.getMessage(),e);
        } finally {
            return facetList;
        }

    }

    public App createApp() throws InstallException {
        App createdApp = null;
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(app);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(BaseUrl + "app"))
                    .header(API_KEY, app.getApiKey())
                    .version(HttpClient.Version.HTTP_1_1)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .timeout(Duration.ofMillis(10000))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            handleAuthenticationError(response);
            ObjectMapper objectMapper = new ObjectMapper();
            createdApp = objectMapper.readValue(response.body(), App.class);
        } catch (InstallException e) {
            throw e;
        } catch (java.lang.Exception e) {
            throw new InstallException(e);
        }
        return createdApp;
    }

    public void createFacet(Facet facetDTO) {
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(facetDTO);
            HttpClient client = HttpClient.newBuilder()
                    .executor(threadPool)
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(BaseUrl + "facet/backend"))
                    .version(HttpClient.Version.HTTP_1_1)
                    .header(API_KEY, app.getApiKey())
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .timeout(Duration.ofMillis(10000))
                    .build();
            CompletableFuture<HttpResponse<String>> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        } catch (Throwable e) {
            logger.error(e.getMessage(),e);
        }
    }

    public Configuration fetchConfiguration(String property, String id) {
        Configuration configuration = null;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(BaseUrl + "facet/configuration?property=" + property + "&id=" + id))
                    .version(HttpClient.Version.HTTP_1_1)
                    .header(API_KEY, app.getApiKey())
                    .GET()
                    .timeout(Duration.ofMillis(10000))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            handleAuthenticationError(response);
            ObjectMapper objectMapper = new ObjectMapper();
            configuration = objectMapper.readValue(response.body(), Configuration.class);
        } catch (Throwable e) {
            logger.error(e.getMessage(),e);
        }
        return configuration;
    }

    public Object fetchConfiguration(String property, String id, String field, Class clazz) {
        Object object = null;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(BaseUrl + "facet/configuration?property=" + property + "&id=" + id))
                    .version(HttpClient.Version.HTTP_1_1)
                    .header(API_KEY, app.getApiKey())
                    .GET()
                    .timeout(Duration.ofMillis(10000))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            handleAuthenticationError(response);
            ObjectMapper objectMapper = new ObjectMapper();
            String responseBody = response.body();
            JsonNode productNode = new ObjectMapper().readTree(responseBody);
            object = objectMapper.readValue(productNode.get(field).toString(), clazz);
        } catch (Throwable e) {
            logger.error(e.getMessage(),e);
        }
        return object;
    }

    public List<Object> fetchConfigurationList(String property, String id, String field, Class clazz) {
        List<Object> objectList = null;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(BaseUrl + "facet/configuration?property=" + property + "&id=" + id))
                    .version(HttpClient.Version.HTTP_1_1)
                    .header(API_KEY, app.getApiKey())
                    .GET()
                    .timeout(Duration.ofMillis(10000))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            handleAuthenticationError(response);
            ObjectMapper objectMapper = new ObjectMapper();
            String responseBody = response.body();
            JsonNode rootNode = new ObjectMapper().readTree(responseBody);
            CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
            objectList = objectMapper.readValue(rootNode.get(field).toString(), listType);
        } catch (Throwable e) {
            logger.error(e.getMessage(),e);
        }
        return objectList;
    }
    public List<Configuration> fetchConfigurations(String property, String id) {
        List<Configuration> configurations = null;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(BaseUrl + "facet/configurations?property=" + property + "&id=" + id))
                    .header(API_KEY, app.getApiKey())
                    .version(HttpClient.Version.HTTP_1_1)
                    .GET()
                    .timeout(Duration.ofMillis(10000))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            handleAuthenticationError(response);
            ObjectMapper objectMapper = new ObjectMapper();
            configurations = objectMapper.readValue(response.body(), new TypeReference<List<Configuration>>() {});
        } catch (Throwable e) {
            logger.error(e.getMessage(),e);
        }
        return configurations;
    }


    private static class MyThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(false);
            return thread;
        }
    }
}