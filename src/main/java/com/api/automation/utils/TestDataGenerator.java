package com.api.automation.utils;

import com.api.automation.models.User;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Test Data Generator utility using Java Faker
 * Provides methods to generate realistic test data
 */
@Slf4j
public class TestDataGenerator {
    
    private static final Faker faker = new Faker(Locale.ENGLISH);
    
    /**
     * Generate a random user
     * @return User with random data
     */
    public static User generateRandomUser() {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String email = generateUniqueEmail(firstName, lastName);
        
        return User.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .username(email)
                .password(generateSecurePassword())
                .phone(faker.phoneNumber().phoneNumber())
                .avatar(faker.internet().avatar())
                .status("active")
                .role("user")
                .isActive(true)
                .emailVerified(faker.bool().bool())
                .build();
    }
    
    /**
     * Generate a random admin user
     * @return Admin user with random data
     */
    public static User generateRandomAdminUser() {
        User user = generateRandomUser();
        user.setRole("admin");
        user.setEmailVerified(true);
        return user;
    }
    
    /**
     * Generate multiple random users
     * @param count Number of users to generate
     * @return List of users
     */
    public static List<User> generateRandomUsers(int count) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            users.add(generateRandomUser());
        }
        return users;
    }
    
    /**
     * Generate a user with specific role
     * @param role User role
     * @return User with specified role
     */
    public static User generateUserWithRole(String role) {
        User user = generateRandomUser();
        user.setRole(role);
        
        // Set email verification based on role
        if ("admin".equals(role) || "moderator".equals(role)) {
            user.setEmailVerified(true);
        }
        
        return user;
    }
    
    /**
     * Generate a user for update testing (partial data)
     * @return User with partial data for PATCH operations
     */
    public static User generateUserForUpdate() {
        return User.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .phone(faker.phoneNumber().phoneNumber())
                .build();
    }
    
    /**
     * Generate a unique email address
     * @param firstName First name
     * @param lastName Last name
     * @return Unique email
     */
    public static String generateUniqueEmail(String firstName, String lastName) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String domain = faker.internet().domainName();
        
        return String.format("%s.%s.%s@%s", 
                firstName.toLowerCase(), 
                lastName.toLowerCase(), 
                timestamp.substring(timestamp.length() - 6), 
                domain);
    }
    
    /**
     * Generate a unique email address
     * @return Unique email
     */
    public static String generateUniqueEmail() {
        return generateUniqueEmail(faker.name().firstName(), faker.name().lastName());
    }
    
    /**
     * Generate a secure password
     * @return Secure password
     */
    public static String generateSecurePassword() {
        return faker.internet().password(8, 16, true, true, true);
    }
    
    /**
     * Generate a random phone number
     * @return Phone number
     */
    public static String generatePhoneNumber() {
        return faker.phoneNumber().phoneNumber();
    }
    
    /**
     * Generate a random company name
     * @return Company name
     */
    public static String generateCompanyName() {
        return faker.company().name();
    }
    
    /**
     * Generate a random address
     * @return Address string
     */
    public static String generateAddress() {
        return faker.address().fullAddress();
    }
    
    /**
     * Generate a random date within the last year
     * @return LocalDateTime
     */
    public static LocalDateTime generateRecentDate() {
        return LocalDateTime.now().minusDays(faker.number().numberBetween(1, 365));
    }
    
    /**
     * Generate a random boolean value
     * @return Boolean
     */
    public static Boolean generateRandomBoolean() {
        return faker.bool().bool();
    }
    
    /**
     * Generate a random ID
     * @return Long ID
     */
    public static Long generateRandomId() {
        return faker.number().numberBetween(1L, 10000L);
    }
    
    /**
     * Generate invalid email for negative testing
     * @return Invalid email
     */
    public static String generateInvalidEmail() {
        String[] invalidEmails = {
                "invalid-email",
                "test@",
                "@example.com",
                "test..test@example.com",
                "test@.com",
                "test@com",
                ""
        };
        return invalidEmails[faker.number().numberBetween(0, invalidEmails.length)];
    }
    
    /**
     * Generate a user with invalid data for negative testing
     * @return User with invalid data
     */
    public static User generateInvalidUser() {
        return User.builder()
                .email(generateInvalidEmail())
                .firstName("") // Empty first name
                .lastName("") // Empty last name
                .username(null) // Null username
                .password("123") // Weak password
                .phone("invalid-phone")
                .build();
    }
}