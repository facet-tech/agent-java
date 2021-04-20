package run.facet.agent.java;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import run.facet.agent.java.exception.InstallException;

import java.lang.instrument.Instrumentation;
import java.util.Objects;

public class Agent {
    public static void premain(String args, Instrumentation instrumentation) {
        try {
            ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
            Transformer transformer = ctx.getBean(Transformer.class);
            instrumentation.addTransformer(transformer);
        } catch (Throwable throwable) {
            System.err.println("An exception prevented the facet agent from starting.");
            printFriendlyException(throwable);
        }
    }

    public static void printFriendlyException(Throwable throwable) {
        Throwable rootCause = findRootCause(throwable);
        if(rootCause instanceof InstallException) {
            rootCause.printStackTrace();
        } else {
            throwable.printStackTrace();
        }
    }

    public static Throwable findRootCause(Throwable throwable) {
        Objects.requireNonNull(throwable);
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }
}
