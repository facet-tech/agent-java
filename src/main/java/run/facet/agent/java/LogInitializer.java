package run.facet.agent.java;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

@Component
public class LogInitializer {

    private final String logName = "/facet.log";
    private Logger logger;

    @Autowired
    public LogInitializer(Properties properties) throws URISyntaxException {
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        LoggerContext loggerContext = new LoggerContext("facet");
        builder.setLoggerContext(loggerContext);
        AppenderComponentBuilder file = builder.newAppender("log", "File");
        file.addAttribute("fileName", properties.getJarPath() + logName);

        LayoutComponentBuilder standard = builder.newLayout("PatternLayout");
        standard.addAttribute("pattern", "%d [%t] %-5level: %msg%n");
        file.add(standard);
        builder.add(file);


        RootLoggerComponentBuilder rootLogger = builder.newRootLogger(Level.ALL);
        rootLogger.add(builder.newAppenderRef("log"));
        builder.add(rootLogger);
        Configurator.initialize(builder.build());

        logger = LogManager.getRootLogger();
        logger.info("\n\n\nFacet Agent Starting...");
    }

    public Logger getLogger() {
        return logger;
    }
}
