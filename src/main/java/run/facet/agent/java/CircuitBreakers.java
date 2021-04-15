package run.facet.agent.java;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class CircuitBreakers {
    private String id = "JAVA~1";
    private String property = "CIRCUIT_BREAKER~";
    private List<CircuitBreaker> circuitBreakers;
    private static Map<String, CircuitBreaker> circuitBreakerMap;

    private Timer timer;
    private int cacheRefreshInterval = 30000;

    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private WebRequest webRequest;
    private LogInitializer logInitializer;
    private Logger logger;

    public CircuitBreakers() {
        circuitBreakers = new ArrayList<>();
        circuitBreakerMap = new HashMap<>();
    }

    @Autowired
    private CircuitBreakers(WebRequest webRequest, LogInitializer logInitializer) {
        this.logInitializer = logInitializer;
        this.logger = logInitializer.getLogger();
        this.webRequest = webRequest;
        fetchCircuitBreakerList();
        timer = new Timer();
        timer.schedule(new CircuitBreakerTimer(), cacheRefreshInterval, cacheRefreshInterval);
    }

    private void fetchCircuitBreakerList() {

        try {
            List<CircuitBreaker> circuitBreakers = (List<CircuitBreaker>) (Object) webRequest.fetchConfigurationList(this.property, this.id, "attribute", CircuitBreaker.class);
            Map<String, CircuitBreaker> circuitBreakerMap = createMap(circuitBreakers);
            lock.writeLock().lock();
            this.circuitBreakers = circuitBreakers;
            this.circuitBreakerMap = circuitBreakerMap;
            lock.writeLock().unlock();
        } catch (java.lang.Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private Map<String, CircuitBreaker> createMap(List<CircuitBreaker> circuitBreakers) {
        Map<String, CircuitBreaker> circuitBreakerMap = new HashMap<>();
        for (CircuitBreaker circuitBreaker : circuitBreakers) {
            circuitBreakerMap.put(circuitBreaker.getReturnType(), circuitBreaker);
        }
        return circuitBreakerMap;
    }

    public CircuitBreaker getBreaker(String returnType) {
        if (circuitBreakerMap.containsKey(returnType)) {
            return circuitBreakerMap.get(returnType);
        } else {
            return circuitBreakerMap.get("default");
        }
    }


    private class CircuitBreakerTimer extends TimerTask {
        @Override
        public void run() {
            fetchCircuitBreakerList();
        }
    }
}
