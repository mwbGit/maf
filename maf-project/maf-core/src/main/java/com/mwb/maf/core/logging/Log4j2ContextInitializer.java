package com.mwb.maf.core.logging;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Log4j2ContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final String FILE_PROTOCOL = "file";
    private static final String DEFAULT_LOG_LEVEL = "info";

    private ConfigurableApplicationContext applicationContext;

    private static final String LOG_PATH = "app.logging.path";
    private static final String DEFAULT_LOG_PATH = "/data/logs/mwb";

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        this.applicationContext = configurableApplicationContext;
        String path = this.applicationContext.getEnvironment().getProperty(LOG_PATH);
        if (StringUtils.isEmpty(path)) {
            path = DEFAULT_LOG_PATH;
        }
        if (StringUtils.isEmpty(path)) {
            System.out.println("no log path specialed.the log won't be printed.");
            return;
        }

        String level = this.applicationContext.getEnvironment().getProperty("app.logging.level");
        if (StringUtils.isEmpty(level)) {
            level = DEFAULT_LOG_LEVEL;
        }
        System.setProperty("app.logging.path", path);
        System.setProperty("app.logging.level", level);

        String location = getPackagedConfigFile("log4j2-caf.xml");
        if (StringUtils.endsWithIgnoreCase(System.getProperty("app.logging.mode"), "dev")) {
            location = getPackagedConfigFile("log4j2-caf-dev.xml");
        }
        try {
            LoggerContext loggerContext = LoggerContext.getContext(false);
            URL url = ResourceUtils.getURL(location);
            ConfigurationSource source = getConfigurationSource(url);
            Configuration logConfiguration = ConfigurationFactory.getInstance().getConfiguration(loggerContext, source);
            loggerContext.start(logConfiguration);
        } catch (Exception ex) {
            throw new IllegalStateException("Could not initialize Log4J2 logging from " + location, ex);
        }
    }

    protected final String getPackagedConfigFile(String fileName) {
        String defaultPath = ClassUtils.getPackageName(getClass());
        defaultPath = defaultPath.replace('.', '/');
        defaultPath = defaultPath + "/" + fileName;
        defaultPath = "classpath:" + defaultPath;
        return defaultPath;
    }

    private ConfigurationSource getConfigurationSource(URL url) throws IOException {
        InputStream stream = url.openStream();
        if (FILE_PROTOCOL.equals(url.getProtocol())) {
            return new ConfigurationSource(stream, ResourceUtils.getFile(url));
        }
        return new ConfigurationSource(stream, url);
    }
}
