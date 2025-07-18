package com.api.automation.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * User model for API requests and responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("first_name")
    private String firstName;
    
    @JsonProperty("last_name")
    private String lastName;
    
    @JsonProperty("username")
    private String username;
    
    @JsonProperty("password")
    private String password;
    
    @JsonProperty("phone")
    private String phone;
    
    @JsonProperty("avatar")
    private String avatar;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("role")
    private String role;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    
    @JsonProperty("last_login")
    private LocalDateTime lastLogin;
    
    @JsonProperty("is_active")
    private Boolean isActive;
    
    @JsonProperty("email_verified")
    private Boolean emailVerified;
    
    // Helper methods for common user scenarios
    
    /**
     * Create a basic user for testing
     * @param email User email
     * @param firstName First name
     * @param lastName Last name
     * @return User instance
     */
    public static User createBasicUser(String email, String firstName, String lastName) {
        return User.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .username(email)
                .isActive(true)
                .emailVerified(false)
                .role("user")
                .status("active")
                .build();
    }
    
    /**
     * Create an admin user for testing
     * @param email User email
     * @param firstName First name
     * @param lastName Last name
     * @return User instance
     */
    public static User createAdminUser(String email, String firstName, String lastName) {
        return User.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .username(email)
                .isActive(true)
                .emailVerified(true)
                .role("admin")
                .status("active")
                .build();
    }
    
    /**
     * Get full name
     * @return Full name
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        }
        return "";
    }
}