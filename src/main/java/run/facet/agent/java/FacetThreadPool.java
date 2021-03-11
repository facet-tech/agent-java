package run.facet.agent.java;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class FacetThreadPool {
    private static final int maxThreads = 20;
    private ExecutorService threadPool = Executors.newFixedThreadPool(maxThreads,new FacetThreadFactory());
    private static FacetThreadPool singleton = null;

    private FacetThreadPool() {}

    public static FacetThreadPool getFacetThreadPool() {
        if(singleton == null) {
            singleton = new FacetThreadPool();
        }
        return singleton;
    }

    public void createFacet(Facet facet) {
        Runnable thread = new FacetThread(facet);
        threadPool.execute(thread);
    }

    public class FacetThread implements Runnable {
        private Facet facet;
        public FacetThread(Facet facet) {
            this.facet = facet;
        }
        @Override
        public void run() {
            WebRequest.createFacet(facet);
        }
    }

    public class FacetThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        }
    }
}
