package main.java.agent;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Facets {
    private List<Facet> facets;
    private Map<String, Facet> facetMap;
    private App app;
    private Timer timer;
    private int refreshInterval = 10000;
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Toggle toggle = Toggle.getToggle();

    public Facets(App app) {
        this.app = app;
        this.facetMap = new HashMap<>();
        fetchFacets();
        timer = new Timer(true);
        timer.schedule(new FacetTimer(), refreshInterval,refreshInterval);
    }

    private void fetchFacets() {
        List<Facet> facets = WebRequest.fetchFacet(app);
        lock.writeLock().lock();
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
            toggle.updateToggle(facet.getFullyQualifiedName(),signature.getSignature(),signature.isEnabled());
        }
    }

    public void add(Facet facet) {
        lock.writeLock().lock();
        try {
            if (!contains(facet)) {
                this.facets.add(facet);
                updateFacetMaps(facet);
                WebRequest.createFacet(facet);
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
