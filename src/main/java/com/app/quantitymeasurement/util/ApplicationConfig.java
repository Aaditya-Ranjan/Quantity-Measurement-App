package com.app.quantitymeasurement.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ApplicationConfig - Loads configuration from application.properties.
 * Supports environment-specific overrides via system properties.
 * UC16: Centralized configuration management.
 */
public class ApplicationConfig {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
    private static final String PROPERTIES_FILE = "application.properties";
    private static ApplicationConfig instance;
    private final Properties properties;

    private ApplicationConfig() {
        properties = new Properties();
        loadProperties();
    }

    public static ApplicationConfig getInstance() {
        if (instance == null) {
            synchronized (ApplicationConfig.class) {
                if (instance == null) {
                    instance = new ApplicationConfig();
                }
            }
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {
            if (is != null) {
                properties.load(is);
                logger.info("Loaded configuration from {}", PROPERTIES_FILE);
            } else {
                logger.warn("Could not find {}. Using defaults.", PROPERTIES_FILE);
            }
        } catch (IOException e) {
            logger.error("Failed to load configuration: {}", e.getMessage());
        }
    }

    /**
     * Gets a property value; system property overrides file property.
     */
    public String getProperty(String key, String defaultValue) {
        String systemProp = System.getProperty(key);
        if (systemProp != null) return systemProp;
        return properties.getProperty(key, defaultValue);
    }

    public String getDbUrl() {
        return getProperty("db.url",
                "jdbc:h2:mem:quantitydb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
    }

    public String getDbUsername() { return getProperty("db.username", "sa"); }
    public String getDbPassword() { return getProperty("db.password", ""); }
    public String getDbDriver()   { return getProperty("db.driver", "org.h2.Driver"); }
    public int    getPoolSize()   { return Integer.parseInt(getProperty("db.pool.size", "5")); }
    public long   getPoolTimeout(){ return Long.parseLong(getProperty("db.pool.timeout", "30000")); }

    public String getRepositoryType() {
        return getProperty("repository.type", "cache");
    }
}