package run.facet.agent.java;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class CircuitBreaker {
    private String id = "JAVA~";
    private String property = "CIRCUIT_BREAKER~";
    private static Map<String,Breaker> circuitBreakerMap;

    private Timer timer;
    private int cacheRefreshInterval = 30000;

    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private WebRequest webRequest;

    @Autowired
    private CircuitBreaker(WebRequest webRequest) {
        this.webRequest = webRequest;
        fetchCircuitBreakerList();
        timer = new Timer();
        timer.schedule(new CircuitBreakerTimer(), cacheRefreshInterval,cacheRefreshInterval);
    }

    private void fetchCircuitBreakerList() {
        Configuration configuration = webRequest.fetchConfiguration(this.property,this.id);
        Map<String,Breaker> newCircuitBreakerMap = convertConfigurationToCircuitBreakerList(configuration);
        lock.writeLock().lock();
        try {
            circuitBreakerMap = newCircuitBreakerMap;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Map<String,Breaker> convertConfigurationToCircuitBreakerList(Configuration configuration) {
        Map<String,Object> attribute = configuration.getAttribute();
        Map<String,Breaker> circuitBreakerMap = new HashMap<>();

        for (LinkedHashMap test : (List<LinkedHashMap>) attribute.get("circuitBreaker")) {
            Breaker breaker = new Breaker();
            breaker.setSignatureReturnType((String) test.get("signatureReturnType"));
            breaker.setCircuitBreaker((String) test.get("circuitBreaker"));
            circuitBreakerMap.put(breaker.getSignatureReturnType(), breaker);
        }
        return circuitBreakerMap;
    }

    public Breaker getBreaker(String returnType) {
        if(circuitBreakerMap.containsKey(returnType)) {
            return circuitBreakerMap.get(returnType);
        } else {
            return circuitBreakerMap.get("default");
        }
    }

    /*public Breaker getBreaker(Facet facet, Signature signature {
        if(circuitBreakerMap.containsKey(returnType)) {
            return circuitBreakerMap.get(returnType);
        } else {
            return circuitBreakerMap.get("default");
        }
    }*/


    private class CircuitBreakerTimer extends TimerTask {
        @Override
        public void run() {
            fetchCircuitBreakerList();
        }
    }
}
