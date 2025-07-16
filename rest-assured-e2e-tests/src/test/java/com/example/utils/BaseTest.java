package com.example.utils;

import com.example.config.ConfigurationManager;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.util.concurrent.TimeUnit;

/**
 * Base test class for all API tests
 * Provides common setup and utility methods
 */
public class BaseTest {
    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected ConfigurationManager config;
    protected RequestSpecification requestSpec;

    @BeforeClass
    public void setUpClass() {
        config = ConfigurationManager.getInstance();
        configureRestAssured();
        logger.info("Test setup completed for environment: {}", config.getCurrentEnvironment());
    }

    @BeforeMethod
    public void setUpMethod() {
        requestSpec = createRequestSpecification();
    }

    private void configureRestAssured() {
        // Set base URI
        RestAssured.baseURI = config.getApiBaseUrl();
        
        // Set base path if API version is specified
        if (config.getApiVersion() != null) {
            RestAssured.basePath = "/" + config.getApiVersion();
        }

        // Configure timeouts
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", config.getApiTimeout())
                        .setParam("http.socket.timeout", config.getApiTimeout()));

        // Configure logging filters if enabled
        if (config.shouldLogRequests()) {
            RestAssured.filters(new RequestLoggingFilter());
        }
        
        if (config.shouldLogResponses()) {
            RestAssured.filters(new ResponseLoggingFilter());
        }

        logger.info("REST Assured configured with base URL: {}", config.getApiBaseUrl());
    }

    protected RequestSpecification createRequestSpecification() {
        RequestSpecification spec = RestAssured.given()
                .contentType("application/json")
                .accept("application/json");

        // Add authentication based on configuration
        if ("bearer".equalsIgnoreCase(config.getAuthType()) && config.getAuthToken() != null) {
            spec = spec.header("Authorization", "Bearer " + config.getAuthToken());
        } else if (config.getAuthUsername() != null && config.getAuthPassword() != null) {
            spec = spec.auth().basic(config.getAuthUsername(), config.getAuthPassword());
        }

        return spec;
    }

    @Step("Wait for {delay} milliseconds")
    protected void waitFor(long delay) {
        try {
            TimeUnit.MILLISECONDS.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Sleep interrupted", e);
        }
    }

    @Step("Generate unique test identifier")
    protected String generateUniqueId() {
        return "test_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId();
    }

    @Step("Generate test email")
    protected String generateTestEmail() {
        return "test_" + System.currentTimeMillis() + "@example.com";
    }

    @Step("Log test step: {stepDescription}")
    protected void logTestStep(String stepDescription) {
        logger.info("Test Step: {}", stepDescription);
    }
}