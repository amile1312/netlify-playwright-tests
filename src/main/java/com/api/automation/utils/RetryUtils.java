package com.api.automation.utils;

import com.api.automation.config.ConfigManager;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * Utility class for handling retries
 */
@Slf4j
public class RetryUtils {
    
    private final ConfigManager configManager;
    
    public RetryUtils() {
        this.configManager = ConfigManager.getInstance();
    }
    
    /**
     * Execute a request with retry logic
     * @param requestSupplier Supplier that executes the request
     * @return Response
     */
    public Response executeWithRetry(Supplier<Response> requestSupplier) {
        int retryCount = configManager.getIntProperty("retry.count", 3);
        int retryDelay = configManager.getIntProperty("retry.delay", 1000);
        
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= retryCount; attempt++) {
            try {
                Response response = requestSupplier.get();
                
                // Check if response indicates a retriable error
                if (isRetriableStatusCode(response.getStatusCode())) {
                    if (attempt < retryCount) {
                        log.warn("Request failed with status {}, attempt {}/{}, retrying in {}ms", 
                                response.getStatusCode(), attempt, retryCount, retryDelay);
                        Thread.sleep(retryDelay);
                        continue;
                    }
                }
                
                return response;
                
            } catch (Exception e) {
                lastException = e;
                if (attempt < retryCount) {
                    log.warn("Request failed with exception on attempt {}/{}, retrying in {}ms: {}", 
                            attempt, retryCount, retryDelay, e.getMessage());
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ie);
                    }
                } else {
                    log.error("Request failed after {} attempts", retryCount, e);
                }
            }
        }
        
        throw new RuntimeException("Request failed after " + retryCount + " attempts", lastException);
    }
    
    /**
     * Check if status code is retriable
     * @param statusCode HTTP status code
     * @return true if retriable
     */
    private boolean isRetriableStatusCode(int statusCode) {
        // Retry on server errors (5xx) and some client errors
        return statusCode >= 500 || 
               statusCode == 408 || // Request Timeout
               statusCode == 429;   // Too Many Requests
    }
}