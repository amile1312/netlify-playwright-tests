package com.api.automation.base;

import com.api.automation.config.ConfigManager;
import com.api.automation.utils.RestAssuredClient;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.lang.reflect.Method;

/**
 * Base Test class containing common setup and utilities
 * All test classes should extend this class
 */
@Slf4j
public abstract class BaseTest {
    
    protected static ConfigManager configManager;
    protected static RestAssuredClient restClient;
    
    @BeforeSuite(alwaysRun = true)
    public void setupSuite() {
        log.info("Starting test suite setup...");
        
        // Initialize configuration manager
        configManager = ConfigManager.getInstance();
        log.info("Running tests in environment: {}", configManager.getEnvironment());
        
        // Setup REST Assured global configuration
        setupRestAssured();
        
        // Initialize REST client
        restClient = RestAssuredClient.getInstance();
        
        log.info("Test suite setup completed successfully");
    }
    
    @BeforeClass(alwaysRun = true)
    public void setupClass() {
        log.info("Setting up test class: {}", this.getClass().getSimpleName());
    }
    
    @BeforeMethod(alwaysRun = true)
    public void setupMethod(Method method) {
        log.info("Starting test method: {}.{}", 
                this.getClass().getSimpleName(), method.getName());
    }
    
    /**
     * Setup REST Assured global configuration
     */
    private void setupRestAssured() {
        // Set base URI
        RestAssured.baseURI = configManager.getBaseUrl();
        
        // Set base path
        RestAssured.basePath = "/" + configManager.getApiVersion();
        
        // Enable request/response logging in development
        if ("development".equals(configManager.getEnvironment())) {
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        }
        
        // Configure timeouts and connections
        RestAssuredConfig config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", configManager.getConnectTimeout())
                        .setParam("http.socket.timeout", configManager.getSocketTimeout())
                        .setParam("http.connection-manager.timeout", configManager.getRequestTimeout()));
        
        RestAssured.config = config;
        
        // Add Allure filter for reporting
        RestAssured.filters(new AllureRestAssured());
        
        log.info("REST Assured configured with base URI: {}{}", 
                RestAssured.baseURI, RestAssured.basePath);
    }
    
    /**
     * Get the REST client instance
     * @return RestAssuredClient instance
     */
    protected RestAssuredClient getRestClient() {
        return restClient;
    }
    
    /**
     * Get the configuration manager
     * @return ConfigManager instance
     */
    protected ConfigManager getConfig() {
        return configManager;
    }
}