package run.facet.agent.java;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.lang.instrument.Instrumentation;

public class Agent {

    public static void premain(String args, Instrumentation instrumentation) {
        System.out.println("Possible Facets");
        System.out.println("---------------");
        ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        try {
            Transformer transformer = ctx.getBean(Transformer.class);
            instrumentation.addTransformer(transformer);
        } catch (Exception e) {
            System.out.println("An exception prevented the facet agent from starting.");
            e.printStackTrace();
        }
    }
}
