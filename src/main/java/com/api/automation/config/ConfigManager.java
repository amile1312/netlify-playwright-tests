package com.api.automation.config;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration Manager to handle environment-specific properties
 * Follows Singleton pattern for global access
 */
@Slf4j
public class ConfigManager {
    
    private static ConfigManager instance;
    private Properties properties;
    private String environment;
    
    private ConfigManager() {
        loadConfiguration();
    }
    
    /**
     * Get singleton instance of ConfigManager
     * @return ConfigManager instance
     */
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    /**
     * Load configuration based on environment
     */
    private void loadConfiguration() {
        // Get environment from system property, default to development
        environment = System.getProperty("environment", "development");
        
        properties = new Properties();
        String configFile = String.format("/environments/%s.properties", environment);
        
        try (InputStream inputStream = getClass().getResourceAsStream(configFile)) {
            if (inputStream == null) {
                log.error("Configuration file not found: {}", configFile);
                throw new RuntimeException("Configuration file not found: " + configFile);
            }
            
            properties.load(inputStream);
            log.info("Loaded configuration for environment: {}", environment);
            
        } catch (IOException e) {
            log.error("Error loading configuration file: {}", configFile, e);
            throw new RuntimeException("Error loading configuration file: " + configFile, e);
        }
    }
    
    /**
     * Get property value by key
     * @param key Property key
     * @return Property value
     */
    public String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            log.warn("Property not found: {}", key);
        }
        return value;
    }
    
    /**
     * Get property value by key with default value
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value or default value
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Get integer property value
     * @param key Property key
     * @return Integer value
     */
    public int getIntProperty(String key) {
        String value = getProperty(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.error("Invalid integer property: {} = {}", key, value);
            throw new RuntimeException("Invalid integer property: " + key + " = " + value);
        }
    }
    
    /**
     * Get integer property value with default
     * @param key Property key
     * @param defaultValue Default value
     * @return Integer value or default
     */
    public int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.warn("Invalid integer property: {} = {}, using default: {}", key, value, defaultValue);
            return defaultValue;
        }
    }
    
    /**
     * Get boolean property value
     * @param key Property key
     * @return Boolean value
     */
    public boolean getBooleanProperty(String key) {
        String value = getProperty(key);
        return Boolean.parseBoolean(value);
    }
    
    /**
     * Get boolean property value with default
     * @param key Property key
     * @param defaultValue Default value
     * @return Boolean value or default
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
    
    /**
     * Get current environment name
     * @return Environment name
     */
    public String getEnvironment() {
        return environment;
    }
    
    /**
     * Get base URL for API
     * @return Base URL
     */
    public String getBaseUrl() {
        return getProperty("base.url");
    }
    
    /**
     * Get API version
     * @return API version
     */
    public String getApiVersion() {
        return getProperty("api.version", "v1");
    }
    
    /**
     * Get request timeout
     * @return Timeout in milliseconds
     */
    public int getRequestTimeout() {
        return getIntProperty("request.timeout", 30000);
    }
    
    /**
     * Get connect timeout
     * @return Timeout in milliseconds
     */
    public int getConnectTimeout() {
        return getIntProperty("connect.timeout", 10000);
    }
    
    /**
     * Get socket timeout
     * @return Timeout in milliseconds
     */
    public int getSocketTimeout() {
        return getIntProperty("socket.timeout", 30000);
    }
}