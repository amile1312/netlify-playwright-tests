# REST Assured API Automation Framework

A comprehensive REST Assured-based API automation framework following industry best practices and DRY principles. This framework supports end-to-end testing across multiple environments (Development, Staging, Production) and covers all major HTTP methods (GET, POST, PATCH, DELETE).

## 🚀 Features

- **Multi-Environment Support**: Development, Staging, and Production configurations
- **Complete HTTP Methods Coverage**: GET, POST, PATCH, DELETE operations
- **Industry Best Practices**: Page Object Model, DRY principles, SOLID principles
- **Comprehensive Reporting**: Allure reports with detailed test execution information
- **JSON Schema Validation**: Automated response schema validation
- **Parallel Test Execution**: TestNG-based parallel execution support
- **Retry Mechanism**: Built-in retry logic for flaky network conditions
- **Data-Driven Testing**: Java Faker integration for realistic test data generation
- **Logging**: Structured logging with Logback
- **CI/CD Ready**: Maven-based build system with profile support

## 📁 Project Structure

```
rest-assured-automation/
├── src/
│   ├── main/java/com/api/automation/
│   │   ├── config/
│   │   │   └── ConfigManager.java           # Environment configuration management
│   │   ├── models/
│   │   │   └── User.java                    # Data models
│   │   └── utils/
│   │       ├── RestAssuredClient.java       # HTTP client wrapper
│   │       ├── RetryUtils.java              # Retry logic utility
│   │       └── TestDataGenerator.java       # Test data generation
│   └── test/
│       ├── java/com/api/automation/
│       │   ├── base/
│       │   │   └── BaseTest.java            # Base test class
│       │   ├── endpoints/
│       │   │   └── UserEndpoints.java       # API endpoint methods
│       │   └── tests/
│       │       ├── UserGetTests.java        # GET operation tests
│       │       ├── UserPostTests.java       # POST operation tests
│       │       ├── UserPatchTests.java      # PATCH operation tests
│       │       └── UserDeleteTests.java     # DELETE operation tests
│       └── resources/
│           ├── environments/                # Environment configurations
│           │   ├── development.properties
│           │   ├── staging.properties
│           │   └── production.properties
│           ├── schemas/                     # JSON schema files
│           │   ├── users-list-schema.json
│           │   └── user-schema.json
│           ├── testng.xml                   # TestNG configuration
│           ├── logback-test.xml            # Logging configuration
│           └── allure.properties           # Allure reporting configuration
├── pom.xml                                 # Maven dependencies and build configuration
└── README-API-Automation.md               # This file
```

## 🛠️ Technologies Used

- **Java 11+**: Programming language
- **Maven**: Build and dependency management
- **REST Assured 5.3.2**: API testing framework
- **TestNG 7.8.0**: Test execution framework
- **Allure 2.24.0**: Test reporting
- **Jackson 2.15.2**: JSON processing
- **Lombok 1.18.30**: Code generation
- **Java Faker 1.0.2**: Test data generation
- **Logback 1.4.11**: Logging framework

## 🚦 Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- An IDE (IntelliJ IDEA, Eclipse, etc.)

## 📋 Setup Instructions

### 1. Clone the Repository
```bash
git clone <repository-url>
cd rest-assured-automation
```

### 2. Install Dependencies
```bash
mvn clean install
```

### 3. Configure Environment
Update the environment-specific configuration files in `src/test/resources/environments/`:
- `development.properties`
- `staging.properties`
- `production.properties`

## 🏃‍♂️ Running Tests

### Run All Tests (Development Environment)
```bash
mvn clean test
```

### Run Tests for Specific Environment
```bash
# Staging
mvn clean test -Penvironment=staging

# Production
mvn clean test -Penvironment=production
```

### Run Specific Test Classes
```bash
# GET tests only
mvn clean test -Dtest=UserGetTests

# POST tests only
mvn clean test -Dtest=UserPostTests

# PATCH tests only
mvn clean test -Dtest=UserPatchTests

# DELETE tests only
mvn clean test -Dtest=UserDeleteTests
```

### Run Tests by Groups
```bash
# Smoke tests
mvn clean test -Dgroups=smoke

# Regression tests
mvn clean test -Dgroups=regression

# Critical tests
mvn clean test -Dgroups=critical
```

### Parallel Execution
```bash
mvn clean test -DthreadCount=5
```

## 📊 Test Reporting

### Generate Allure Reports
```bash
# Generate and serve Allure report
mvn allure:serve

# Generate Allure report (without serving)
mvn allure:report
```

The Allure report will be available at `target/site/allure-maven-plugin/`

### View Logs
- Application logs: `target/logs/api-automation.log`
- REST Assured logs: `target/logs/rest-assured.log`

## 🧪 Test Coverage

### GET Operations (`UserGetTests.java`)
- ✅ Get all users
- ✅ Get users with pagination
- ✅ Get user by valid ID
- ✅ Get user by invalid ID (404 testing)
- ✅ Search users by email
- ✅ Search users by role
- ✅ Response headers validation
- ✅ Invalid pagination parameters handling

### POST Operations (`UserPostTests.java`)
- ✅ Create user with valid data
- ✅ Create admin user
- ✅ Create user with minimum required fields
- ✅ Create user with invalid email format
- ✅ Create user with missing required fields
- ✅ Create user with duplicate email
- ✅ Create multiple users in sequence
- ✅ Create user with special characters in name
- ✅ Response headers validation

### PATCH Operations (`UserPatchTests.java`)
- ✅ Partially update user with valid data
- ✅ Update user first name only
- ✅ Update user last name only
- ✅ Update user phone number
- ✅ Update user with multiple fields
- ✅ Update non-existent user (404 testing)
- ✅ Update user with invalid field values
- ✅ Update user with empty request body
- ✅ Update user role and status
- ✅ Response headers validation

### DELETE Operations (`UserDeleteTests.java`)
- ✅ Delete user with valid ID
- ✅ Delete non-existent user (404 testing)
- ✅ Delete user and verify it's no longer accessible
- ✅ Delete user with invalid ID format
- ✅ Delete multiple users in sequence
- ✅ Delete user twice (idempotency testing)
- ✅ Delete admin user (special case)
- ✅ Performance testing
- ✅ Response headers validation

## 🔧 Configuration

### Environment Configuration
Each environment has its own configuration file with the following parameters:

```properties
# API Configuration
base.url=https://api-dev.example.com
api.version=v1

# Authentication
auth.enabled=true
auth.type=bearer
auth.token=your-auth-token

# Timeouts
request.timeout=30000
connect.timeout=10000
socket.timeout=30000

# Retry Configuration
retry.count=3
retry.delay=1000

# Logging
log.level=DEBUG
log.requests=true
log.responses=true
```

### Test Data Configuration
The framework uses Java Faker to generate realistic test data:
- Random names, emails, phone numbers
- Unique identifiers to avoid conflicts
- Invalid data for negative testing
- Configurable data patterns

## 🎯 Best Practices Implemented

### 1. **Page Object Model (POM)**
- Endpoint classes (`UserEndpoints.java`) encapsulate API operations
- Separation of test logic from API interaction logic

### 2. **DRY Principle**
- Reusable components and utilities
- Common base test class
- Shared configuration management

### 3. **Single Responsibility Principle**
- Each class has a single, well-defined purpose
- Clear separation of concerns

### 4. **Error Handling**
- Comprehensive retry mechanism
- Graceful handling of network failures
- Detailed error logging

### 5. **Test Data Management**
- Dynamic test data generation
- Environment-specific test data
- Data cleanup strategies

### 6. **Reporting and Logging**
- Detailed Allure reports with screenshots
- Structured logging with different log levels
- Performance metrics tracking

## 🔄 CI/CD Integration

### Maven Profiles
The project supports different Maven profiles for various environments:

```bash
# Development (default)
mvn clean test

# Staging
mvn clean test -Pstaging

# Production
mvn clean test -Pproduction
```

### Jenkins Pipeline Example
```groovy
pipeline {
    agent any
    
    parameters {
        choice(name: 'ENVIRONMENT', choices: ['development', 'staging', 'production'], description: 'Environment to test')
        choice(name: 'TEST_SUITE', choices: ['all', 'smoke', 'regression'], description: 'Test suite to run')
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Test') {
            steps {
                script {
                    if (params.TEST_SUITE == 'all') {
                        sh "mvn clean test -P${params.ENVIRONMENT}"
                    } else {
                        sh "mvn clean test -P${params.ENVIRONMENT} -Dgroups=${params.TEST_SUITE}"
                    }
                }
            }
        }
        
        stage('Report') {
            steps {
                allure([
                    includeProperties: false,
                    jdk: '',
                    properties: [],
                    reportBuildPolicy: 'ALWAYS',
                    results: [[path: 'target/allure-results']]
                ])
            }
        }
    }
}
```

## 🐛 Troubleshooting

### Common Issues

1. **Connection Timeout**
   - Check network connectivity
   - Verify API endpoint URLs
   - Increase timeout values in configuration

2. **Authentication Failures**
   - Verify auth tokens in environment configuration
   - Check token expiration
   - Validate authentication headers

3. **Test Data Conflicts**
   - Ensure unique identifiers in test data
   - Implement proper data cleanup
   - Use dynamic data generation

4. **Parallel Execution Issues**
   - Check for shared state between tests
   - Verify thread-safe implementations
   - Adjust thread count based on system resources

### Debug Mode
Run tests with debug logging:
```bash
mvn clean test -Dlog.level=DEBUG
```

## 📈 Performance Considerations

- **Parallel Execution**: Tests run in parallel for faster execution
- **Connection Pooling**: REST Assured connection pooling for efficiency
- **Retry Logic**: Smart retry mechanism to handle temporary failures
- **Response Time Validation**: All tests include response time assertions
- **Resource Management**: Proper cleanup of resources and connections

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the documentation and examples

---

**Happy Testing! 🎉**