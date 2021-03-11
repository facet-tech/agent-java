package main.java.agent;

import java.lang.instrument.Instrumentation;

public class Agent {
    private static AgentBiz agentBiz = AgentBiz.getAgentBiz();
    public static void premain(String args, Instrumentation instrumentation){
        System.out.println("Possible Facets");
        System.out.println("---------------");
        Transformer transformer = new Transformer();
        transformer.setAgentBiz(agentBiz);
        instrumentation.addTransformer(transformer);
    }
}
