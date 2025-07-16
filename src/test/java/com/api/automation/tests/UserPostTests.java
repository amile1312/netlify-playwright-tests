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
 * Test class for User POST operations
 * Tests user creation endpoints
 */
@Epic("User Management")
@Feature("User POST Operations")
public class UserPostTests extends BaseTest {
    
    private UserEndpoints userEndpoints;
    
    @BeforeClass
    public void setupClass() {
        super.setupClass();
        userEndpoints = new UserEndpoints();
    }
    
    @Test(description = "Create user with valid data")
    @Story("Create User")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test to verify that a new user can be created with valid data")
    public void testCreateUserWithValidData() {
        // Test data
        User newUser = TestDataGenerator.generateRandomUser();
        
        // Execute
        Response response = userEndpoints.createUser(newUser);
        
        // Verify
        response.then()
                .statusCode(201)
                .contentType("application/json")
                .body("id", notNullValue())
                .body("email", equalTo(newUser.getEmail()))
                .body("first_name", equalTo(newUser.getFirstName()))
                .body("last_name", equalTo(newUser.getLastName()))
                .body("createdAt", notNullValue());
        
        // Extract created user ID
        String createdUserId = response.jsonPath().getString("id");
        assertNotNull(createdUserId, "Created user ID should not be null");
        
        // Log creation details
        log.info("User created successfully with ID: {}", createdUserId);
        log.info("Response Time: {} ms", response.getTime());
        
        // Validate response time
        assertTrue(response.getTime() < 5000, "Response time should be less than 5 seconds");
    }
    
    @Test(description = "Create admin user")
    @Story("Create User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify that an admin user can be created")
    public void testCreateAdminUser() {
        // Test data
        User adminUser = TestDataGenerator.generateRandomAdminUser();
        
        // Execute
        Response response = userEndpoints.createUser(adminUser);
        
        // Verify
        response.then()
                .statusCode(201)
                .contentType("application/json")
                .body("id", notNullValue())
                .body("email", equalTo(adminUser.getEmail()))
                .body("role", equalTo("admin"));
        
        log.info("Admin user created successfully");
    }
    
    @Test(description = "Create user with minimum required fields")
    @Story("Create User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify user creation with only required fields")
    public void testCreateUserWithMinimumFields() {
        // Test data - only required fields
        User minimalUser = User.builder()
                .email(TestDataGenerator.generateUniqueEmail())
                .firstName("John")
                .lastName("Doe")
                .build();
        
        // Execute
        Response response = userEndpoints.createUser(minimalUser);
        
        // Verify
        response.then()
                .statusCode(201)
                .contentType("application/json")
                .body("id", notNullValue())
                .body("email", equalTo(minimalUser.getEmail()))
                .body("first_name", equalTo(minimalUser.getFirstName()))
                .body("last_name", equalTo(minimalUser.getLastName()));
        
        log.info("User created with minimum fields");
    }
    
    @Test(description = "Create user with invalid email format")
    @Story("Create User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify that user creation fails with invalid email format")
    public void testCreateUserWithInvalidEmail() {
        // Test data
        User invalidUser = TestDataGenerator.generateRandomUser();
        invalidUser.setEmail(TestDataGenerator.generateInvalidEmail());
        
        // Execute
        Response response = userEndpoints.createUser(invalidUser);
        
        // Verify - should return 400 Bad Request
        int statusCode = response.getStatusCode();
        assertTrue(statusCode == 400 || statusCode == 422, 
                "Status code should be 400 or 422 for invalid email");
        
        response.then()
                .contentType("application/json")
                .body("error", notNullValue());
        
        log.info("User creation failed as expected with invalid email");
    }
    
    @Test(description = "Create user with missing required fields")
    @Story("Create User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify that user creation fails when required fields are missing")
    public void testCreateUserWithMissingRequiredFields() {
        // Test data - missing required fields
        User incompleteUser = User.builder()
                .firstName("John")
                // Missing email and last name
                .build();
        
        // Execute
        Response response = userEndpoints.createUser(incompleteUser);
        
        // Verify - should return 400 Bad Request
        int statusCode = response.getStatusCode();
        assertTrue(statusCode == 400 || statusCode == 422, 
                "Status code should be 400 or 422 for missing required fields");
        
        log.info("User creation failed as expected with missing required fields");
    }
    
    @Test(description = "Create user with duplicate email")
    @Story("Create User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify that user creation fails with duplicate email")
    public void testCreateUserWithDuplicateEmail() {
        // Test data - first user
        User firstUser = TestDataGenerator.generateRandomUser();
        
        // Create first user
        Response firstResponse = userEndpoints.createUser(firstUser);
        
        // Verify first user is created successfully (or assume it exists)
        if (firstResponse.getStatusCode() == 201) {
            log.info("First user created successfully");
            
            // Test data - second user with same email
            User duplicateUser = TestDataGenerator.generateRandomUser();
            duplicateUser.setEmail(firstUser.getEmail());
            
            // Execute - try to create user with duplicate email
            Response duplicateResponse = userEndpoints.createUser(duplicateUser);
            
            // Verify - should return 400 or 409 Conflict
            int statusCode = duplicateResponse.getStatusCode();
            assertTrue(statusCode == 400 || statusCode == 409 || statusCode == 422, 
                    "Status code should be 400, 409, or 422 for duplicate email");
            
            log.info("Duplicate user creation failed as expected");
        } else {
            log.info("First user creation failed, test scenario not applicable");
        }
    }
    
    @Test(description = "Create multiple users in sequence")
    @Story("Create User")
    @Severity(SeverityLevel.MINOR)
    @Description("Test to verify multiple users can be created in sequence")
    public void testCreateMultipleUsers() {
        int numberOfUsers = 3;
        
        for (int i = 0; i < numberOfUsers; i++) {
            // Test data
            User user = TestDataGenerator.generateRandomUser();
            
            // Execute
            Response response = userEndpoints.createUser(user);
            
            // Verify
            response.then()
                    .statusCode(201)
                    .contentType("application/json")
                    .body("id", notNullValue())
                    .body("email", equalTo(user.getEmail()));
            
            log.info("User {} created successfully", i + 1);
        }
        
        log.info("All {} users created successfully", numberOfUsers);
    }
    
    @Test(description = "Create user with special characters in name")
    @Story("Create User")
    @Severity(SeverityLevel.MINOR)
    @Description("Test to verify user creation with special characters in name fields")
    public void testCreateUserWithSpecialCharacters() {
        // Test data with special characters
        User specialUser = User.builder()
                .email(TestDataGenerator.generateUniqueEmail())
                .firstName("José-María")
                .lastName("O'Connor")
                .build();
        
        // Execute
        Response response = userEndpoints.createUser(specialUser);
        
        // Verify
        response.then()
                .statusCode(201)
                .contentType("application/json")
                .body("id", notNullValue())
                .body("first_name", equalTo(specialUser.getFirstName()))
                .body("last_name", equalTo(specialUser.getLastName()));
        
        log.info("User created successfully with special characters in name");
    }
    
    @Test(description = "Verify response headers for POST requests")
    @Story("Response Headers")
    @Severity(SeverityLevel.MINOR)
    @Description("Test to verify correct response headers are returned for POST requests")
    public void testCreateUserResponseHeaders() {
        // Test data
        User user = TestDataGenerator.generateRandomUser();
        
        // Execute
        Response response = userEndpoints.createUser(user);
        
        // Verify headers
        response.then()
                .statusCode(201)
                .header("Content-Type", containsString("application/json"))
                .header("Location", notNullValue());
        
        // Log important headers
        log.info("Content-Type: {}", response.getHeader("Content-Type"));
        log.info("Location: {}", response.getHeader("Location"));
    }
}