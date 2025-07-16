package com.api.automation.endpoints;

import com.api.automation.models.User;
import com.api.automation.utils.RestAssuredClient;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * User Endpoints class following Page Object Model pattern
 * Contains all user-related API endpoints and operations
 */
@Slf4j
public class UserEndpoints {
    
    private final RestAssuredClient restClient;
    private static final String USERS_ENDPOINT = "/users";
    private static final String USER_BY_ID_ENDPOINT = "/users/{id}";
    
    public UserEndpoints() {
        this.restClient = RestAssuredClient.getInstance();
    }
    
    /**
     * Get all users
     * @return Response
     */
    public Response getAllUsers() {
        log.info("Getting all users");
        return restClient.get(USERS_ENDPOINT);
    }
    
    /**
     * Get users with pagination
     * @param page Page number
     * @param perPage Number of users per page
     * @return Response
     */
    public Response getAllUsers(int page, int perPage) {
        log.info("Getting users with pagination - page: {}, per_page: {}", page, perPage);
        
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("page", page);
        queryParams.put("per_page", perPage);
        
        return restClient.get(USERS_ENDPOINT, null, queryParams);
    }
    
    /**
     * Get user by ID
     * @param userId User ID
     * @return Response
     */
    public Response getUserById(Long userId) {
        log.info("Getting user by ID: {}", userId);
        
        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("id", userId);
        
        return restClient.get(USER_BY_ID_ENDPOINT, pathParams);
    }
    
    /**
     * Create a new user
     * @param user User object
     * @return Response
     */
    public Response createUser(User user) {
        log.info("Creating new user with email: {}", user.getEmail());
        return restClient.post(USERS_ENDPOINT, user);
    }
    
    /**
     * Update user (PUT - full update)
     * @param userId User ID
     * @param user User object
     * @return Response
     */
    public Response updateUser(Long userId, User user) {
        log.info("Updating user with ID: {}", userId);
        
        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("id", userId);
        
        return restClient.put(USER_BY_ID_ENDPOINT, user, pathParams);
    }
    
    /**
     * Partially update user (PATCH - partial update)
     * @param userId User ID
     * @param userUpdates User updates
     * @return Response
     */
    public Response partiallyUpdateUser(Long userId, User userUpdates) {
        log.info("Partially updating user with ID: {}", userId);
        
        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("id", userId);
        
        return restClient.patch(USER_BY_ID_ENDPOINT, userUpdates, pathParams);
    }
    
    /**
     * Delete user
     * @param userId User ID
     * @return Response
     */
    public Response deleteUser(Long userId) {
        log.info("Deleting user with ID: {}", userId);
        
        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("id", userId);
        
        return restClient.delete(USER_BY_ID_ENDPOINT, pathParams);
    }
    
    /**
     * Search users by email
     * @param email Email to search
     * @return Response
     */
    public Response searchUsersByEmail(String email) {
        log.info("Searching users by email: {}", email);
        
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("email", email);
        
        return restClient.get(USERS_ENDPOINT, null, queryParams);
    }
    
    /**
     * Search users by role
     * @param role Role to search
     * @return Response
     */
    public Response searchUsersByRole(String role) {
        log.info("Searching users by role: {}", role);
        
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("role", role);
        
        return restClient.get(USERS_ENDPOINT, null, queryParams);
    }
}