package main.java.agent;

public class AgentBiz {
    private App app = WebRequest.createApp(new App("WORKSPACE~N2IzODAyNzQtZGY5OC00OTE4LWEwM2UtZGVjYmRmZTkyMTA4","BackedTestPoc","dev"));
    private CircuitBreaker circuitBreaker = CircuitBreaker.getCircuitBreakerList();
    private BlockList blockList = BlockList.getBlockList();
    private Toggle toggle = Toggle.getToggle();
    private Facets facets = new Facets(app);
    private Frameworks frameworks = Frameworks.getFrameworks();
    private static AgentBiz singleton;

    public static AgentBiz getAgentBiz() {
        if(singleton == null) {
            singleton = new AgentBiz();
        }
        return singleton;
    }

    private AgentBiz() {}

    public  App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public CircuitBreaker getCircuitBreaker() {
        return circuitBreaker;
    }

    public void setCircuitBreaker(CircuitBreaker circuitBreaker) {
        this.circuitBreaker = circuitBreaker;
    }

    public BlockList getBlockList() {
        return blockList;
    }

    public void setBlockList(BlockList blockList) {
        this.blockList = blockList;
    }

    public Toggle getToggle() {
        return toggle;
    }

    public void setToggle(Toggle toggle) {
        this.toggle = toggle;
    }

    public Facets getFacets() {
        return facets;
    }

    public void setFacets(Facets facets) {
        this.facets = facets;
    }

    public static AgentBiz getSingleton() {
        return singleton;
    }

    public static void setSingleton(AgentBiz singleton) {
        AgentBiz.singleton = singleton;
    }

    public Frameworks getFrameworks() {
        return frameworks;
    }

    public void setFrameworks(Frameworks frameworks) {
        this.frameworks = frameworks;
    }
}
