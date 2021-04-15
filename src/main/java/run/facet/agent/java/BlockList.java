package run.facet.agent.java;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class BlockList {
    private String id = "JAVA_PACKAGE_PREFIX~";
    private String property = "BLOCK_LIST~";
    private static Map<String, String> blockList = new HashMap<>() {{
        put("java", "java");
        put("sun", "sun");
        put("jdk", "jdk");
        put("org", "org");
    }};

    private Timer timer;
    private int blockListRefreshIntervalInSeconds = 30000;

    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private WebRequest webRequest;
    private LogInitializer logInitializer;
    private Logger logger;

    @Autowired
    private BlockList(WebRequest webRequest, LogInitializer logInitializer) {
        this.logInitializer = logInitializer;
        this.logger = logInitializer.getLogger();
        this.webRequest = webRequest;
        fetchBlockList();
        timer = new Timer(true);
        timer.schedule(new BlockListTimer(), blockListRefreshIntervalInSeconds, blockListRefreshIntervalInSeconds);
    }

    private void fetchBlockList() {
        try {
            Configuration configuration = webRequest.fetchConfiguration(this.property, this.id);
            Map<String, String> newBlockList = convertConfigurationToBlockList(configuration);
            lock.writeLock().lock();
            blockList = newBlockList;
            lock.writeLock().unlock();
        } catch (java.lang.Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    public Map<String, String> convertConfigurationToBlockList(Configuration configuration) {
        Map<String, Object> attribute = configuration.getAttribute();
        Map<String, String> blockList = new HashMap<>();
        for (String item : (List<String>) attribute.get("signature")) {
            blockList.put(item, item);
        }
        return blockList;
    }

    public boolean contains(String className) {
        String[] packageParts = className.split("/");
        String qualifiedName = "";
        lock.readLock().lock();
        try {
            for (String part : packageParts) {
                if (qualifiedName == "") {
                    qualifiedName = part;
                } else {
                    qualifiedName += "/" + part;
                }
                if (blockList.containsKey(qualifiedName)) {
                    return true;
                }
            }
            return false;
        } finally {
            lock.readLock().unlock();
        }

    }

    private class BlockListTimer extends TimerTask {
        @Override
        public void run() {
            fetchBlockList();
        }
    }
}
