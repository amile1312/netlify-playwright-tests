package com.api.automation.tests;

import com.api.automation.base.BaseTest;
import com.api.automation.endpoints.UserEndpoints;
import com.api.automation.models.User;
import com.api.automation.utils.TestDataGenerator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * Test class for User DELETE operations
 * Tests user deletion endpoints
 */
@Epic("User Management")
@Feature("User DELETE Operations")
public class UserDeleteTests extends BaseTest {
    
    private UserEndpoints userEndpoints;
    
    @BeforeClass
    public void setupClass() {
        super.setupClass();
        userEndpoints = new UserEndpoints();
    }
    
    @Test(description = "Delete user with valid ID")
    @Story("Delete User")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test to verify that a user can be deleted with valid ID")
    public void testDeleteUserWithValidId() {
        // Test data - assuming user with ID 2 exists
        Long userIdToDelete = 2L;
        
        // Execute
        Response response = userEndpoints.deleteUser(userIdToDelete);
        
        // Verify
        response.then()
                .statusCode(204); // No Content - successful deletion
        
        // Log deletion details
        log.info("User {} deleted successfully", userIdToDelete);
        log.info("Response Time: {} ms", response.getTime());
        
        // Validate response time
        assertTrue(response.getTime() < 5000, "Response time should be less than 5 seconds");
        
        // Verify the response body is empty
        String responseBody = response.getBody().asString();
        assertTrue(responseBody.isEmpty() || responseBody.trim().isEmpty(), 
                "Response body should be empty for successful deletion");
    }
    
    @Test(description = "Delete non-existent user returns 404")
    @Story("Delete User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify that deleting a non-existent user returns 404")
    public void testDeleteNonExistentUser() {
        // Test data - using a non-existent user ID
        Long nonExistentUserId = 99999L;
        
        // Execute
        Response response = userEndpoints.deleteUser(nonExistentUserId);
        
        // Verify
        response.then()
                .statusCode(404)
                .contentType("application/json");
        
        log.info("Delete failed as expected for non-existent user ID: {}", nonExistentUserId);
    }
    
    @Test(description = "Delete user and verify it's no longer accessible")
    @Story("Delete User")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test to verify that after deletion, user is no longer accessible via GET")
    public void testDeleteUserAndVerifyNotAccessible() {
        // Step 1: Create a user first
        User newUser = TestDataGenerator.generateRandomUser();
        Response createResponse = userEndpoints.createUser(newUser);
        
        // Check if user creation was successful
        if (createResponse.getStatusCode() == 201) {
            String createdUserId = createResponse.jsonPath().getString("id");
            log.info("User created with ID: {}", createdUserId);
            
            // Step 2: Delete the created user
            Response deleteResponse = userEndpoints.deleteUser(Long.valueOf(createdUserId));
            deleteResponse.then().statusCode(204);
            log.info("User {} deleted successfully", createdUserId);
            
            // Step 3: Verify user is no longer accessible
            Response getResponse = userEndpoints.getUserById(Long.valueOf(createdUserId));
            getResponse.then().statusCode(404);
            log.info("Verified user {} is no longer accessible after deletion", createdUserId);
        } else {
            log.warn("User creation failed, skipping deletion verification test");
        }
    }
    
    @Test(description = "Delete user with invalid ID format")
    @Story("Delete User")
    @Severity(SeverityLevel.MINOR)
    @Description("Test to verify handling of invalid ID format in delete requests")
    public void testDeleteUserWithInvalidIdFormat() {
        // Note: This test would typically involve sending a request with invalid ID format
        // However, with strongly typed methods, we'll test with 0 as an edge case
        Long invalidUserId = 0L;
        
        // Execute
        Response response = userEndpoints.deleteUser(invalidUserId);
        
        // Verify - should return 400 Bad Request or 404 Not Found
        int statusCode = response.getStatusCode();
        assertTrue(statusCode == 400 || statusCode == 404, 
                "Status code should be 400 or 404 for invalid ID");
        
        log.info("Delete with invalid ID handled with status code: {}", statusCode);
    }
    
    @Test(description = "Delete multiple users in sequence")
    @Story("Delete User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify that multiple users can be deleted in sequence")
    public void testDeleteMultipleUsers() {
        // Test data - assuming these users exist or create them first
        Long[] userIdsToDelete = {3L, 4L, 5L};
        
        for (Long userId : userIdsToDelete) {
            // Execute
            Response response = userEndpoints.deleteUser(userId);
            
            // Verify - should be 204 (deleted) or 404 (already deleted/doesn't exist)
            int statusCode = response.getStatusCode();
            assertTrue(statusCode == 204 || statusCode == 404, 
                    "Status code should be 204 or 404");
            
            log.info("Delete request for user {} returned status: {}", userId, statusCode);
        }
        
        log.info("Multiple user deletion requests completed");
    }
    
    @Test(description = "Delete user twice (idempotency test)")
    @Story("Delete User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify idempotent behavior of delete operations")
    public void testDeleteUserTwice() {
        // Test data
        Long userId = 6L;
        
        // First deletion
        Response firstDeleteResponse = userEndpoints.deleteUser(userId);
        int firstStatusCode = firstDeleteResponse.getStatusCode();
        log.info("First delete returned status: {}", firstStatusCode);
        
        // Second deletion (should be idempotent)
        Response secondDeleteResponse = userEndpoints.deleteUser(userId);
        int secondStatusCode = secondDeleteResponse.getStatusCode();
        log.info("Second delete returned status: {}", secondStatusCode);
        
        // Verify - both should succeed or second should return 404
        assertTrue((firstStatusCode == 204 && secondStatusCode == 404) || 
                   (firstStatusCode == 404 && secondStatusCode == 404) ||
                   (firstStatusCode == 204 && secondStatusCode == 204), 
                "Delete operations should be idempotent");
    }
    
    @Test(description = "Verify response headers for DELETE requests")
    @Story("Response Headers")
    @Severity(SeverityLevel.MINOR)
    @Description("Test to verify correct response headers are returned for DELETE requests")
    public void testDeleteUserResponseHeaders() {
        // Test data
        Long userId = 7L;
        
        // Execute
        Response response = userEndpoints.deleteUser(userId);
        
        // Verify headers based on status code
        if (response.getStatusCode() == 204) {
            // Successful deletion
            assertNotNull(response.getHeader("Date"), "Date header should be present");
        } else if (response.getStatusCode() == 404) {
            // User not found
            response.then()
                    .header("Content-Type", containsString("application/json"));
        }
        
        // Log important headers
        log.info("Status Code: {}", response.getStatusCode());
        log.info("Content-Type: {}", response.getHeader("Content-Type"));
        log.info("Date: {}", response.getHeader("Date"));
    }
    
    @Test(description = "Delete user and verify response time")
    @Story("Performance")
    @Severity(SeverityLevel.MINOR)
    @Description("Test to verify delete operation response time is within acceptable limits")
    public void testDeleteUserPerformance() {
        // Test data
        Long userId = 8L;
        
        // Execute
        long startTime = System.currentTimeMillis();
        Response response = userEndpoints.deleteUser(userId);
        long endTime = System.currentTimeMillis();
        
        // Calculate response time
        long responseTime = endTime - startTime;
        
        // Verify
        assertTrue(responseTime < 5000, 
                "Delete operation should complete within 5 seconds");
        assertTrue(response.getTime() < 5000, 
                "REST Assured measured response time should be within 5 seconds");
        
        log.info("Delete operation completed in {} ms", responseTime);
        log.info("REST Assured measured time: {} ms", response.getTime());
    }
    
    @Test(description = "Delete admin user (special case)")
    @Story("Delete User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify deletion of admin users")
    public void testDeleteAdminUser() {
        // Create an admin user first
        User adminUser = TestDataGenerator.generateRandomAdminUser();
        Response createResponse = userEndpoints.createUser(adminUser);
        
        if (createResponse.getStatusCode() == 201) {
            String adminUserId = createResponse.jsonPath().getString("id");
            log.info("Admin user created with ID: {}", adminUserId);
            
            // Delete the admin user
            Response deleteResponse = userEndpoints.deleteUser(Long.valueOf(adminUserId));
            
            // Verify - deletion should succeed
            deleteResponse.then().statusCode(204);
            log.info("Admin user {} deleted successfully", adminUserId);
        } else {
            log.warn("Admin user creation failed, skipping admin deletion test");
        }
    }
}