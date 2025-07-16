package com.api.automation.utils;

import com.api.automation.config.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * REST Assured Client utility class
 * Provides common HTTP operations with built-in retry and logging
 */
@Slf4j
public class RestAssuredClient {
    
    private static RestAssuredClient instance;
    private final ConfigManager configManager;
    private final RetryUtils retryUtils;
    
    private RestAssuredClient() {
        this.configManager = ConfigManager.getInstance();
        this.retryUtils = new RetryUtils();
    }
    
    /**
     * Get singleton instance
     * @return RestAssuredClient instance
     */
    public static synchronized RestAssuredClient getInstance() {
        if (instance == null) {
            instance = new RestAssuredClient();
        }
        return instance;
    }
    
    /**
     * Create base request specification with common headers
     * @return RequestSpecification
     */
    private RequestSpecification createBaseRequest() {
        RequestSpecification request = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
        
        // Add authentication if enabled
        if (configManager.getBooleanProperty("auth.enabled", false)) {
            String authType = configManager.getProperty("auth.type", "bearer");
            String token = configManager.getProperty("auth.token");
            
            if ("bearer".equalsIgnoreCase(authType) && token != null) {
                request.header("Authorization", "Bearer " + token);
            }
        }
        
        return request;
    }
    
    /**
     * Perform GET request
     * @param endpoint API endpoint
     * @return Response
     */
    public Response get(String endpoint) {
        return get(endpoint, null, null);
    }
    
    /**
     * Perform GET request with path parameters
     * @param endpoint API endpoint
     * @param pathParams Path parameters
     * @return Response
     */
    public Response get(String endpoint, Map<String, Object> pathParams) {
        return get(endpoint, pathParams, null);
    }
    
    /**
     * Perform GET request with path parameters and query parameters
     * @param endpoint API endpoint
     * @param pathParams Path parameters
     * @param queryParams Query parameters
     * @return Response
     */
    public Response get(String endpoint, Map<String, Object> pathParams, Map<String, Object> queryParams) {
        return retryUtils.executeWithRetry(() -> {
            RequestSpecification request = createBaseRequest();
            
            if (pathParams != null) {
                request.pathParams(pathParams);
            }
            
            if (queryParams != null) {
                request.queryParams(queryParams);
            }
            
            log.info("Sending GET request to: {}", endpoint);
            Response response = request.get(endpoint);
            
            logResponse("GET", endpoint, response);
            return response;
        });
    }
    
    /**
     * Perform POST request
     * @param endpoint API endpoint
     * @param requestBody Request body
     * @return Response
     */
    public Response post(String endpoint, Object requestBody) {
        return post(endpoint, requestBody, null);
    }
    
    /**
     * Perform POST request with path parameters
     * @param endpoint API endpoint
     * @param requestBody Request body
     * @param pathParams Path parameters
     * @return Response
     */
    public Response post(String endpoint, Object requestBody, Map<String, Object> pathParams) {
        return retryUtils.executeWithRetry(() -> {
            RequestSpecification request = createBaseRequest();
            
            if (pathParams != null) {
                request.pathParams(pathParams);
            }
            
            if (requestBody != null) {
                request.body(requestBody);
            }
            
            log.info("Sending POST request to: {}", endpoint);
            Response response = request.post(endpoint);
            
            logResponse("POST", endpoint, response);
            return response;
        });
    }
    
    /**
     * Perform PUT request
     * @param endpoint API endpoint
     * @param requestBody Request body
     * @return Response
     */
    public Response put(String endpoint, Object requestBody) {
        return put(endpoint, requestBody, null);
    }
    
    /**
     * Perform PUT request with path parameters
     * @param endpoint API endpoint
     * @param requestBody Request body
     * @param pathParams Path parameters
     * @return Response
     */
    public Response put(String endpoint, Object requestBody, Map<String, Object> pathParams) {
        return retryUtils.executeWithRetry(() -> {
            RequestSpecification request = createBaseRequest();
            
            if (pathParams != null) {
                request.pathParams(pathParams);
            }
            
            if (requestBody != null) {
                request.body(requestBody);
            }
            
            log.info("Sending PUT request to: {}", endpoint);
            Response response = request.put(endpoint);
            
            logResponse("PUT", endpoint, response);
            return response;
        });
    }
    
    /**
     * Perform PATCH request
     * @param endpoint API endpoint
     * @param requestBody Request body
     * @return Response
     */
    public Response patch(String endpoint, Object requestBody) {
        return patch(endpoint, requestBody, null);
    }
    
    /**
     * Perform PATCH request with path parameters
     * @param endpoint API endpoint
     * @param requestBody Request body
     * @param pathParams Path parameters
     * @return Response
     */
    public Response patch(String endpoint, Object requestBody, Map<String, Object> pathParams) {
        return retryUtils.executeWithRetry(() -> {
            RequestSpecification request = createBaseRequest();
            
            if (pathParams != null) {
                request.pathParams(pathParams);
            }
            
            if (requestBody != null) {
                request.body(requestBody);
            }
            
            log.info("Sending PATCH request to: {}", endpoint);
            Response response = request.patch(endpoint);
            
            logResponse("PATCH", endpoint, response);
            return response;
        });
    }
    
    /**
     * Perform DELETE request
     * @param endpoint API endpoint
     * @return Response
     */
    public Response delete(String endpoint) {
        return delete(endpoint, null);
    }
    
    /**
     * Perform DELETE request with path parameters
     * @param endpoint API endpoint
     * @param pathParams Path parameters
     * @return Response
     */
    public Response delete(String endpoint, Map<String, Object> pathParams) {
        return retryUtils.executeWithRetry(() -> {
            RequestSpecification request = createBaseRequest();
            
            if (pathParams != null) {
                request.pathParams(pathParams);
            }
            
            log.info("Sending DELETE request to: {}", endpoint);
            Response response = request.delete(endpoint);
            
            logResponse("DELETE", endpoint, response);
            return response;
        });
    }
    
    /**
     * Log response details based on configuration
     * @param method HTTP method
     * @param endpoint API endpoint
     * @param response Response object
     */
    private void logResponse(String method, String endpoint, Response response) {
        if (configManager.getBooleanProperty("log.responses", false)) {
            log.info("{} {} - Status: {}, Time: {}ms", 
                    method, endpoint, response.getStatusCode(), response.getTime());
            
            if (log.isDebugEnabled()) {
                log.debug("Response body: {}", response.getBody().asString());
            }
        }
    }
}