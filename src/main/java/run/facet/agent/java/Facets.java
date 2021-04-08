package run.facet.agent.java;

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

    //TODO fix race condition where facets could be overwritten by the timer during parsing and vice versa.
    @Autowired
    public Facets(App app, WebRequest webRequest, Toggles toggles) {
        this.webRequest = webRequest;
        this.app = app;
        this.facetMap = new HashMap<>();
        this.toggles = toggles;
        fetchFacets();
        timer = new Timer(true);
        timer.schedule(new FacetTimer(), refreshInterval,refreshInterval);
    }

    private void fetchFacets() {
        List<Facet> facets = webRequest.fetchFacet(app);lock.writeLock().lock();
        try {
            for (Facet facet : facets) {
                updateFacetMaps(facet);
            }
            this.facets = facets;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void updateFacetMaps(Facet facet) {
        facetMap.put(facet.getFullyQualifiedName(), facet);
        for (Signature signature : facet.getSignature()) {
            toggles.updateToggle(facet.getFullyQualifiedName(),signature.getSignature(),signature.isEnabled());
        }
    }

    public void add(Facet facet) {
        lock.writeLock().lock();
        try {
            //TODO add support for facets changes
            if (!contains(facet)) {
                this.facets.add(facet);
                updateFacetMaps(facet);
                webRequest.createFacet(facet);
            }
        } finally {
            lock.writeLock().unlock();
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
