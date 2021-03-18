package run.facet.agent.java;

import java.lang.instrument.Instrumentation;

public class Agent {
    private static AgentBiz agentBiz;

    static {
        try {
            agentBiz = AgentBiz.getAgentBiz();
        } catch (Exception e) {
            System.out.println("An exception prevented the facet agent from starting.");
            e.printStackTrace();
            agentBiz = null;
        }
    }

    public static void premain(String args, Instrumentation instrumentation){
        System.out.println("Possible Facets");
        System.out.println("---------------");
        if(agentBiz != null) {
            Transformer transformer = new Transformer();
            transformer.setAgentBiz(agentBiz);
            instrumentation.addTransformer(transformer);
        }
    }
}
