package run.facet.agent.java;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class Facets {
    private List<Facet> facets;
    private Map<String, Facet> facetMap;
    private App app;
    private Timer timer;
    private int refreshInterval = 10000;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    private Toggles toggles;
    private WebRequest webRequest;
    private LogInitializer logInitializer;
    private Logger logger;

    //TODO fix race condition where facets could be overwritten by the timer during parsing and vice versa.
    @Autowired
    public Facets(App app, WebRequest webRequest, Toggles toggles, LogInitializer logInitializer) {
        this.logInitializer = logInitializer;
        this.logger = logInitializer.getLogger();
        this.webRequest = webRequest;
        this.app = app;
        this.facetMap = new HashMap<>();
        this.toggles = toggles;
        fetchFacets();
        timer = new Timer(true);
        timer.schedule(new FacetTimer(), refreshInterval, refreshInterval);
    }

    private void fetchFacets() {
        try {
            List<Facet> facets = webRequest.fetchFacet();
            Map<String, Facet> facetMap = updateFacetMaps(facets);
            lock.writeLock().lock();
            this.facets = facets;
            this.facetMap = facetMap;
            lock.writeLock().unlock();
        } catch (java.lang.Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private Map<String, Facet> updateFacetMaps(List<Facet> facets) {
        Map<String, Facet> facetMap = new HashMap<>();
        for (Facet facet : facets) {
            facetMap.put(facet.getFullyQualifiedName(), facet);
            updateToggles(facet);
        }
        return facetMap;
    }

    public void updateToggles(Facet facet) {
        for (Signature signature : facet.getSignature()) {
            toggles.updateToggle(facet.getFullyQualifiedName(), signature.getSignature(), signature.isEnabled());
        }
    }

    public void add(Facet facet) {
        //TODO add support for facets changes
        if (!contains(facet)) {
            lock.writeLock().lock();
            this.facets.add(facet);
            facetMap.put(facet.getFullyQualifiedName(), facet);
            updateToggles(facet);
            lock.writeLock().unlock();
            webRequest.createFacet(facet);
        }

    }

    public boolean contains(Facet facet) {
        return facetMap.containsKey(facet.getFullyQualifiedName());
    }

    public Facet get(String name) {
        return facetMap.get(name);
    }

    private class FacetTimer extends TimerTask {
        @Override
        public void run() {
            fetchFacets();
        }
    }
}
