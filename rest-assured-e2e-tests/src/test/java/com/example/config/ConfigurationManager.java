package com.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration Manager for handling environment-specific properties
 */
public class ConfigurationManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);
    private static ConfigurationManager instance;
    private Properties properties;
    private String currentEnvironment;

    private ConfigurationManager() {
        loadConfiguration();
    }

    public static synchronized ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    private void loadConfiguration() {
        currentEnvironment = System.getProperty("environment", "development");
        properties = new Properties();
        
        String configFile = String.format("config/%s.properties", currentEnvironment);
        
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (inputStream == null) {
                logger.error("Configuration file not found: {}", configFile);
                throw new RuntimeException("Configuration file not found: " + configFile);
            }
            
            properties.load(inputStream);
            logger.info("Loaded configuration for environment: {}", currentEnvironment);
            
        } catch (IOException e) {
            logger.error("Failed to load configuration file: {}", configFile, e);
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer value for property {}: {}. Using default: {}", key, value, defaultValue);
            return defaultValue;
        }
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    public String getCurrentEnvironment() {
        return currentEnvironment;
    }

    // API Configuration methods
    public String getApiBaseUrl() {
        return getProperty("api.base.url");
    }

    public String getApiVersion() {
        return getProperty("api.version", "v1");
    }

    public int getApiTimeout() {
        return getIntProperty("api.timeout", 30000);
    }

    // Authentication Configuration methods
    public String getAuthType() {
        return getProperty("auth.type", "bearer");
    }

    public String getAuthToken() {
        return getProperty("auth.token");
    }

    public String getAuthUsername() {
        return getProperty("auth.username");
    }

    public String getAuthPassword() {
        return getProperty("auth.password");
    }

    // Test Configuration methods
    public boolean shouldCreateTestUsers() {
        return getBooleanProperty("test.data.create.users", true);
    }

    public boolean shouldCleanupTestData() {
        return getBooleanProperty("test.data.cleanup", true);
    }

    // Retry Configuration methods
    public int getMaxRetryAttempts() {
        return getIntProperty("retry.max.attempts", 3);
    }

    public int getRetryDelay() {
        return getIntProperty("retry.delay.ms", 1000);
    }

    // Logging Configuration methods
    public boolean shouldLogRequests() {
        return getBooleanProperty("log.requests", false);
    }

    public boolean shouldLogResponses() {
        return getBooleanProperty("log.responses", false);
    }
}