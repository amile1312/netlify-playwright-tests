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
 * Test class for User PATCH operations
 * Tests partial user update endpoints
 */
@Epic("User Management")
@Feature("User PATCH Operations")
public class UserPatchTests extends BaseTest {
    
    private UserEndpoints userEndpoints;
    
    @BeforeClass
    public void setupClass() {
        super.setupClass();
        userEndpoints = new UserEndpoints();
    }
    
    @Test(description = "Partially update user with valid data")
    @Story("Update User")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test to verify that a user can be partially updated with valid data")
    public void testPartiallyUpdateUserWithValidData() {
        // Test data - assuming user with ID 2 exists
        Long userId = 2L;
        User updateData = TestDataGenerator.generateUserForUpdate();
        
        // Execute
        Response response = userEndpoints.partiallyUpdateUser(userId, updateData);
        
        // Verify
        response.then()
                .statusCode(200)
                .contentType("application/json")
                .body("first_name", equalTo(updateData.getFirstName()))
                .body("last_name", equalTo(updateData.getLastName()))
                .body("updatedAt", notNullValue());
        
        // Log update details
        log.info("User {} updated successfully", userId);
        log.info("Response Time: {} ms", response.getTime());
        
        // Validate response time
        assertTrue(response.getTime() < 5000, "Response time should be less than 5 seconds");
    }
    
    @Test(description = "Update user first name only")
    @Story("Update User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify that only the first name can be updated")
    public void testUpdateUserFirstNameOnly() {
        // Test data
        Long userId = 2L;
        String newFirstName = "UpdatedFirstName";
        
        User updateData = User.builder()
                .firstName(newFirstName)
                .build();
        
        // Execute
        Response response = userEndpoints.partiallyUpdateUser(userId, updateData);
        
        // Verify
        response.then()
                .statusCode(200)
                .contentType("application/json")
                .body("first_name", equalTo(newFirstName))
                .body("updatedAt", notNullValue());
        
        log.info("User first name updated successfully to: {}", newFirstName);
    }
    
    @Test(description = "Update user last name only")
    @Story("Update User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify that only the last name can be updated")
    public void testUpdateUserLastNameOnly() {
        // Test data
        Long userId = 2L;
        String newLastName = "UpdatedLastName";
        
        User updateData = User.builder()
                .lastName(newLastName)
                .build();
        
        // Execute
        Response response = userEndpoints.partiallyUpdateUser(userId, updateData);
        
        // Verify
        response.then()
                .statusCode(200)
                .contentType("application/json")
                .body("last_name", equalTo(newLastName))
                .body("updatedAt", notNullValue());
        
        log.info("User last name updated successfully to: {}", newLastName);
    }
    
    @Test(description = "Update user phone number")
    @Story("Update User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify that user phone number can be updated")
    public void testUpdateUserPhoneNumber() {
        // Test data
        Long userId = 2L;
        String newPhone = TestDataGenerator.generatePhoneNumber();
        
        User updateData = User.builder()
                .phone(newPhone)
                .build();
        
        // Execute
        Response response = userEndpoints.partiallyUpdateUser(userId, updateData);
        
        // Verify
        response.then()
                .statusCode(200)
                .contentType("application/json")
                .body("phone", equalTo(newPhone))
                .body("updatedAt", notNullValue());
        
        log.info("User phone updated successfully to: {}", newPhone);
    }
    
    @Test(description = "Update user with multiple fields")
    @Story("Update User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify that multiple user fields can be updated simultaneously")
    public void testUpdateUserMultipleFields() {
        // Test data
        Long userId = 2L;
        User updateData = User.builder()
                .firstName("NewFirst")
                .lastName("NewLast")
                .phone(TestDataGenerator.generatePhoneNumber())
                .status("inactive")
                .build();
        
        // Execute
        Response response = userEndpoints.partiallyUpdateUser(userId, updateData);
        
        // Verify
        response.then()
                .statusCode(200)
                .contentType("application/json")
                .body("first_name", equalTo(updateData.getFirstName()))
                .body("last_name", equalTo(updateData.getLastName()))
                .body("phone", equalTo(updateData.getPhone()))
                .body("status", equalTo(updateData.getStatus()))
                .body("updatedAt", notNullValue());
        
        log.info("User updated successfully with multiple fields");
    }
    
    @Test(description = "Update non-existent user returns 404")
    @Story("Update User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify that updating a non-existent user returns 404")
    public void testUpdateNonExistentUser() {
        // Test data
        Long nonExistentUserId = 99999L;
        User updateData = TestDataGenerator.generateUserForUpdate();
        
        // Execute
        Response response = userEndpoints.partiallyUpdateUser(nonExistentUserId, updateData);
        
        // Verify
        response.then()
                .statusCode(404)
                .contentType("application/json");
        
        log.info("Update failed as expected for non-existent user ID: {}", nonExistentUserId);
    }
    
    @Test(description = "Update user with invalid field values")
    @Story("Update User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify that updating with invalid field values is handled correctly")
    public void testUpdateUserWithInvalidValues() {
        // Test data
        Long userId = 2L;
        User updateData = User.builder()
                .firstName("") // Empty first name
                .phone("invalid-phone-format")
                .build();
        
        // Execute
        Response response = userEndpoints.partiallyUpdateUser(userId, updateData);
        
        // Verify - should return 400 Bad Request or handle gracefully
        int statusCode = response.getStatusCode();
        assertTrue(statusCode == 400 || statusCode == 422 || statusCode == 200, 
                "Status code should be 400, 422, or 200 (handled gracefully)");
        
        if (statusCode != 200) {
            response.then()
                    .contentType("application/json")
                    .body("error", notNullValue());
        }
        
        log.info("Update with invalid values handled with status code: {}", statusCode);
    }
    
    @Test(description = "Update user with empty request body")
    @Story("Update User")
    @Severity(SeverityLevel.MINOR)
    @Description("Test to verify that PATCH with empty body is handled correctly")
    public void testUpdateUserWithEmptyBody() {
        // Test data
        Long userId = 2L;
        User emptyUpdateData = User.builder().build();
        
        // Execute
        Response response = userEndpoints.partiallyUpdateUser(userId, emptyUpdateData);
        
        // Verify - should return 400 or handle gracefully
        int statusCode = response.getStatusCode();
        assertTrue(statusCode == 400 || statusCode == 200, 
                "Status code should be 400 or 200 for empty update");
        
        log.info("Empty body update handled with status code: {}", statusCode);
    }
    
    @Test(description = "Update user role")
    @Story("Update User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify that user role can be updated")
    public void testUpdateUserRole() {
        // Test data
        Long userId = 2L;
        String newRole = "moderator";
        
        User updateData = User.builder()
                .role(newRole)
                .build();
        
        // Execute
        Response response = userEndpoints.partiallyUpdateUser(userId, updateData);
        
        // Verify
        response.then()
                .statusCode(200)
                .contentType("application/json")
                .body("role", equalTo(newRole))
                .body("updatedAt", notNullValue());
        
        log.info("User role updated successfully to: {}", newRole);
    }
    
    @Test(description = "Update user status")
    @Story("Update User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify that user status can be updated")
    public void testUpdateUserStatus() {
        // Test data
        Long userId = 2L;
        String newStatus = "suspended";
        
        User updateData = User.builder()
                .status(newStatus)
                .build();
        
        // Execute
        Response response = userEndpoints.partiallyUpdateUser(userId, updateData);
        
        // Verify
        response.then()
                .statusCode(200)
                .contentType("application/json")
                .body("status", equalTo(newStatus))
                .body("updatedAt", notNullValue());
        
        log.info("User status updated successfully to: {}", newStatus);
    }
    
    @Test(description = "Verify response headers for PATCH requests")
    @Story("Response Headers")
    @Severity(SeverityLevel.MINOR)
    @Description("Test to verify correct response headers are returned for PATCH requests")
    public void testPatchUserResponseHeaders() {
        // Test data
        Long userId = 2L;
        User updateData = TestDataGenerator.generateUserForUpdate();
        
        // Execute
        Response response = userEndpoints.partiallyUpdateUser(userId, updateData);
        
        // Verify headers
        response.then()
                .statusCode(200)
                .header("Content-Type", containsString("application/json"));
        
        // Log important headers
        log.info("Content-Type: {}", response.getHeader("Content-Type"));
        log.info("Last-Modified: {}", response.getHeader("Last-Modified"));
    }
}