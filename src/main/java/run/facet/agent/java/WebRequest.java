package run.facet.agent.java;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;

public class WebRequest {
    private static ExecutorService threadPool = Executors.newFixedThreadPool(20,new MyThreadFactory());

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Performing some shutdown cleanup...");
            threadPool.shutdown();
            while (true) {
                try {

                    System.out.println("Waiting for the service to terminate...");
                    if (threadPool.awaitTermination(30, TimeUnit.SECONDS)) {
                        break;
                    }
                } catch (InterruptedException e) {
                }
            }
            System.out.println("Done cleaning");
        }));
    }

    public static List<Facet> fetchFacet(App app) {
        List<Facet> facetList = null;
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.facet.run/facet/backend?appId=" + app.getName()))
                    .version(HttpClient.Version.HTTP_1_1)
                    .GET()
                    .timeout(Duration.ofMillis(10000))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper objectMapper = new ObjectMapper();
            facetList = objectMapper.readValue(response.body(), new TypeReference<List<Facet>>(){});
        } catch (Exception e) {
            System.out.println(e);
        } catch (Throwable e) {
            System.out.println(e);
            e.printStackTrace();
        } finally {
            return facetList;
        }

    }

    public static App createApp(App app) {
        App createdApp = null;
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(app);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.facet.run/app"))
                    .version(HttpClient.Version.HTTP_1_1)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .timeout(Duration.ofMillis(10000))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper objectMapper = new ObjectMapper();
            createdApp = objectMapper.readValue(response.body(), App.class);
        } catch (Exception e) {
            System.out.println(e);
        } catch (Throwable e) {
            System.out.println(e);
            e.printStackTrace();
        } finally {
            return createdApp;
        }
    }

    public static void createFacet(Facet facetDTO) {
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(facetDTO);
            //HttpClient client = HttpClient.newHttpClient();
            HttpClient client = HttpClient.newBuilder()
                    .executor(threadPool)
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.facet.run/facet/backend"))
                    .version(HttpClient.Version.HTTP_1_1)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .timeout(Duration.ofMillis(10000))
                    .build();
           // HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            CompletableFuture<HttpResponse<String>> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println(e);
        } catch (Throwable e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    public static Configuration fetchConfiguration(String property, String id) {
        Configuration configuration = null;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.facet.run/facet/configuration?property=" + property + "&id=" + id))
                    .version(HttpClient.Version.HTTP_1_1)
                    .GET()
                    .timeout(Duration.ofMillis(10000))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper objectMapper = new ObjectMapper();
            configuration = objectMapper.readValue(response.body(), Configuration.class);
        } catch (Exception e) {
            System.out.println(e);
        } catch (Throwable e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return configuration;
    }

    public static  List<Configuration> fetchConfigurations(String property, String id) {
        List<Configuration> configurations = null;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.facet.run/facet/configurations?property=" + property + "&id=" + id))
                    .version(HttpClient.Version.HTTP_1_1)
                    .GET()
                    .timeout(Duration.ofMillis(10000))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper objectMapper = new ObjectMapper();
            configurations = objectMapper.readValue(response.body(), new TypeReference<List<Configuration>>(){});
        } catch (Exception e) {
            System.out.println(e);
        } catch (Throwable e) {
            System.out.println(e);
            e.printStackTrace();
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