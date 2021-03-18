package run.facet.agent.java;

public class AgentBiz {
    private App app;
    private CircuitBreaker circuitBreaker;
    private BlockList blockList;
    private Toggle toggle;
    private Facets facets;/
    private Frameworks frameworks;
    private static AgentBiz singleton;
    private Properties properties;

    public static AgentBiz getAgentBiz() throws Exception {
        if(singleton == null) {
            singleton = new AgentBiz();
        }
        return singleton;
    }

    private AgentBiz() throws Exception {
        properties = new Properties();
        app = WebRequest.createApp((App) properties.getProperty(App.class));
        circuitBreaker = CircuitBreaker.getCircuitBreakerList();
        blockList = BlockList.getBlockList();
        toggle = Toggle.getToggle();
        facets = new Facets(app);
        frameworks = Frameworks.getFrameworks();
    }

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
