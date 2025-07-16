package com.api.automation.tests;

import com.api.automation.base.BaseTest;
import com.api.automation.endpoints.UserEndpoints;
import com.api.automation.models.User;
import com.api.automation.utils.TestDataGenerator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * Test class for User GET operations
 * Tests all GET endpoints for user management
 */
@Epic("User Management")
@Feature("User GET Operations")
public class UserGetTests extends BaseTest {
    
    private UserEndpoints userEndpoints;
    
    @BeforeClass
    public void setupClass() {
        super.setupClass();
        userEndpoints = new UserEndpoints();
    }
    
    @Test(description = "Get all users successfully")
    @Story("Get All Users")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test to verify that all users can be retrieved successfully")
    public void testGetAllUsers() {
        // Execute
        Response response = userEndpoints.getAllUsers();
        
        // Verify
        response.then()
                .statusCode(200)
                .contentType("application/json")
                .body("page", greaterThan(0))
                .body("per_page", greaterThan(0))
                .body("total", greaterThanOrEqualTo(0))
                .body("total_pages", greaterThanOrEqualTo(0))
                .body("data", notNullValue())
                .body("data", isA(java.util.List.class));
        
        // Log response details
        log.info("Response Status: {}", response.getStatusCode());
        log.info("Response Time: {} ms", response.getTime());
        
        // Validate response time
        assertTrue(response.getTime() < 5000, "Response time should be less than 5 seconds");
    }
    
    @Test(description = "Get users with pagination")
    @Story("Get Users with Pagination")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify pagination works correctly for user listing")
    public void testGetUsersWithPagination() {
        // Test data
        int page = 1;
        int perPage = 5;
        
        // Execute
        Response response = userEndpoints.getAllUsers(page, perPage);
        
        // Verify
        response.then()
                .statusCode(200)
                .contentType("application/json")
                .body("page", equalTo(page))
                .body("per_page", equalTo(perPage))
                .body("data", hasSize(lessThanOrEqualTo(perPage)));
        
        // Verify JSON schema
        response.then()
                .body(matchesJsonSchemaInClasspath("schemas/users-list-schema.json"));
    }
    
    @Test(description = "Get user by valid ID")
    @Story("Get User by ID")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test to verify that a user can be retrieved by valid ID")
    public void testGetUserByValidId() {
        // Test data - using a known existing user ID or create one first
        Long userId = 1L; // Assuming user with ID 1 exists
        
        // Execute
        Response response = userEndpoints.getUserById(userId);
        
        // Verify
        response.then()
                .statusCode(200)
                .contentType("application/json")
                .body("data.id", equalTo(userId.intValue()))
                .body("data.email", notNullValue())
                .body("data.first_name", notNullValue())
                .body("data.last_name", notNullValue())
                .body("data.avatar", notNullValue());
        
        // Extract and validate user data
        User user = response.jsonPath().getObject("data", User.class);
        assertNotNull(user, "User should not be null");
        assertEquals(user.getId(), userId, "User ID should match requested ID");
        assertNotNull(user.getEmail(), "User email should not be null");
        assertTrue(user.getEmail().contains("@"), "Email should contain @ symbol");
    }
    
    @Test(description = "Get user by invalid ID returns 404")
    @Story("Get User by ID")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify that requesting a non-existent user returns 404")
    public void testGetUserByInvalidId() {
        // Test data - using a non-existent user ID
        Long invalidUserId = 99999L;
        
        // Execute
        Response response = userEndpoints.getUserById(invalidUserId);
        
        // Verify
        response.then()
                .statusCode(404)
                .contentType("application/json");
    }
    
    @Test(description = "Get users with invalid pagination parameters")
    @Story("Get Users with Pagination")
    @Severity(SeverityLevel.MINOR)
    @Description("Test to verify handling of invalid pagination parameters")
    public void testGetUsersWithInvalidPagination() {
        // Test data - invalid pagination
        int invalidPage = -1;
        int invalidPerPage = 0;
        
        // Execute
        Response response = userEndpoints.getAllUsers(invalidPage, invalidPerPage);
        
        // Verify - should either return 400 or handle gracefully with default values
        int statusCode = response.getStatusCode();
        assertTrue(statusCode == 400 || statusCode == 200, 
                "Status code should be either 400 (bad request) or 200 (handled gracefully)");
        
        if (statusCode == 200) {
            // If handled gracefully, verify default values are used
            response.then()
                    .body("page", greaterThan(0))
                    .body("per_page", greaterThan(0));
        }
    }
    
    @Test(description = "Search users by email")
    @Story("Search Users")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify users can be searched by email")
    public void testSearchUsersByEmail() {
        // Test data - using a known email pattern
        String emailToSearch = "janet.weaver@reqres.in"; // Known test email
        
        // Execute
        Response response = userEndpoints.searchUsersByEmail(emailToSearch);
        
        // Verify
        response.then()
                .statusCode(200)
                .contentType("application/json");
        
        // If results are found, verify they contain the searched email
        if (response.jsonPath().getList("data").size() > 0) {
            response.then()
                    .body("data.findAll { it.email == '" + emailToSearch + "' }", 
                            hasSize(greaterThan(0)));
        }
    }
    
    @Test(description = "Search users by role")
    @Story("Search Users")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify users can be searched by role")
    public void testSearchUsersByRole() {
        // Test data
        String roleToSearch = "admin";
        
        // Execute
        Response response = userEndpoints.searchUsersByRole(roleToSearch);
        
        // Verify
        response.then()
                .statusCode(200)
                .contentType("application/json");
        
        // If results are found, verify they have the correct role
        if (response.jsonPath().getList("data").size() > 0) {
            response.then()
                    .body("data.findAll { it.role == '" + roleToSearch + "' }", 
                            hasSize(greaterThan(0)));
        }
    }
    
    @Test(description = "Verify response headers for GET requests")
    @Story("Response Headers")
    @Severity(SeverityLevel.MINOR)
    @Description("Test to verify correct response headers are returned")
    public void testGetUsersResponseHeaders() {
        // Execute
        Response response = userEndpoints.getAllUsers();
        
        // Verify headers
        response.then()
                .statusCode(200)
                .header("Content-Type", containsString("application/json"))
                .header("Cache-Control", notNullValue());
        
        // Log important headers
        log.info("Content-Type: {}", response.getHeader("Content-Type"));
        log.info("Cache-Control: {}", response.getHeader("Cache-Control"));
        log.info("X-Response-Time: {}", response.getHeader("X-Response-Time"));
    }
}