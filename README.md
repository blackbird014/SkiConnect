# SkiConnect

SkiConnect is a REST API application for connecting ski teachers with students, with role-based access and place-based search.

## Features

- User management with role-based access control
- School and teacher management
- Lesson availability and booking
- Place-based search for schools and teachers
- OpenAPI/Swagger documentation
- Automatic database schema generation from JPA entities

## Technologies

- Java 17
- Spring Boot 3.2.3
- Spring Data JPA
- Spring Security
- H2 Database (for development)
- OpenAPI/Swagger

## Project Structure

```
SkiConnect/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── skiconnect/
│       │           ├── controller/    # REST API endpoints
│       │           │   ├── LessonController.java
│       │           │   ├── PlaceController.java
│       │           │   ├── SearchController.java
│       │           │   └── UserController.java
│       │           ├── service/       # Business logic
│       │           │   ├── BaseService.java
│       │           │   ├── BaseServiceImpl.java
│       │           │   ├── LessonAvailabilityService.java
│       │           │   ├── LessonAvailabilityServiceImpl.java
│       │           │   ├── PlaceService.java
│       │           │   ├── PlaceServiceImpl.java
│       │           │   ├── SchoolService.java
│       │           │   ├── SchoolServiceImpl.java
│       │           │   ├── TeacherService.java
│       │           │   ├── TeacherServiceImpl.java
│       │           │   ├── UserService.java
│       │           │   └── UserServiceImpl.java
│       │           ├── model/         # Domain models
│       │           │   ├── Lesson.java
│       │           │   ├── LessonAvailability.java
│       │           │   ├── Place.java
│       │           │   ├── Role.java
│       │           │   ├── School.java
│       │           │   ├── SearchResponse.java
│       │           │   ├── Teacher.java
│       │           │   └── User.java
│       │           ├── repository/    # Data access
│       │           │   ├── LessonAvailabilityRepository.java
│       │           │   ├── PlaceRepository.java
│       │           │   ├── SchoolRepository.java
│       │           │   ├── TeacherRepository.java
│       │           │   └── UserRepository.java
│       │           ├── config/        # Configuration
│       │           │   ├── OpenApiConfig.java
│       │           │   └── SecurityConfig.java
│       │           └── SkiConnectApplication.java
│       └── resources/
│           ├── application.properties # Application configuration
│           ├── schema.sql            # Additional SQL schema
│           ├── data.sql              # Initial data
│           └── api/                  # API documentation
│               └── skiconnect-api.yaml
├── pom.xml                           # Maven dependencies
├── README.md                         # Project documentation
└── ARCHITECTURE.md                   # Architecture documentation
```

For detailed architecture information, please refer to [ARCHITECTURE.md](ARCHITECTURE.md).

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Building the Application

```bash
mvn clean install
```

This will build the application, run tests, generate test coverage reports, and perform code quality analysis.

### Running the Application

```bash
mvn spring-boot:run
```

The application will start on http://localhost:8080/api/v1

### Database Configuration

The application uses JPA/Hibernate to automatically generate the database schema from entity classes. Additional SQL scripts are provided for:

- Performance optimization (indexes)
- Additional constraints
- Initial data population

The configuration can be found in `application.properties`:

```properties
# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Schema Initialization
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:schema.sql
spring.sql.init.data-locations=classpath:data.sql
```

### Default Admin User

The application automatically creates a default admin user during initialization:

- **Username:** admin
- **Password:** admin123
- **Email:** admin@skiconnect.com

This user has full administrative privileges and can manage users, places, and assign roles.

### Generating OpenAPI Specification

This project uses SpringDoc to automatically generate an OpenAPI 3 specification based on the implemented REST controllers.

To obtain the specification in YAML format, ensure the application is running and execute the following command in your terminal:

```bash
curl http://localhost:8080/v3/api-docs.yaml > generated-openapi.yaml
```

Alternatively, you can get the JSON format:

```bash
curl http://localhost:8080/v3/api-docs > generated-openapi.json
```

This generated file (`generated-openapi.yaml` or `generated-openapi.json`) will accurately reflect the current state of the API and can be used for UI development or other integrations.

### Test Coverage and Code Quality

#### Test Coverage Report

To generate a test coverage report:

```bash
mvn jacoco:report
```

The report will be available at: `target/site/jacoco/index.html`

#### Code Quality Analysis

To run code quality analysis:

```bash
mvn spotbugs:spotbugs
```

The report will be available at: `target/spotbugs.html`

### Accessing the API Documentation

Once the application is running, you can access the Swagger UI at:

http://localhost:8080/api/v1/swagger-ui.html

### H2 Console

For development purposes, you can access the H2 console at:

http://localhost:8080/api/v1/h2-console

Use the following credentials:
- JDBC URL: jdbc:h2:mem:skiconnectdb
- Username: sa
- Password: password

After connecting, you can log in to the application using the default admin user:
- Username: admin
- Password: admin123

## API Endpoints

### Places

- `POST /api/v1/places` - Create a new place (admin only)

### Users

- `POST /api/v1/users` - Create a new user with optional CV (admin only)
- `POST /api/v1/users/{userId}/roles` - Assign a role to a user (admin only)

### Lessons

- `POST /api/v1/lessons` - Create lesson availability (ski_school only)
- `GET /api/v1/lessons` - List lessons (filtered by role in future)
- `POST /api/v1/lessons/{lessonId}/book` - Book a lesson (student/student_group)

### Search

- `GET /api/v1/search` - Search schools and teachers by place

## Security

The application uses Spring Security with role-based access control. The following roles are defined:

- ADMIN - Can manage users, places, and assign roles
- SKI_SCHOOL - Can create lesson availabilities
- STUDENT - Can book lessons
- STUDENT_GROUP - Can book lessons for groups

## License

This project is licensed under the MIT License - see the LICENSE file for details. 

## Design Assessment

### Strengths

1. **Clean Architecture**
   - Clear separation of concerns with distinct layers (controllers, services, repositories)
   - Well-organized package structure following standard Spring Boot conventions
   - Base service pattern implementation promoting code reuse

2. **Security Implementation**
   - Comprehensive role-based access control (RBAC)
   - JWT-based authentication with proper token validation
   - Method-level security using @PreAuthorize annotations
   - Protected endpoints with appropriate role requirements

3. **API Design**
   - RESTful principles followed consistently
   - Clear endpoint naming and versioning (/api/v1/...)
   - OpenAPI/Swagger documentation for better API discoverability
   - Proper use of HTTP methods and status codes

4. **Testing**
   - Comprehensive integration tests for authentication flows
   - Unit tests for domain models
   - Test coverage reporting with JaCoCo
   - Code quality analysis with SpotBugs

5. **Database Design**
   - Proper entity relationships (User-School-Teacher-Lesson)
   - Appropriate use of JPA annotations
   - Unique constraints on critical fields
   - Foreign key relationships maintaining data integrity
   - Automatic schema generation from entities
   - Performance optimization through indexes

### Areas for Improvement

1. **Error Handling**
   - Could benefit from a centralized exception handling mechanism
   - More detailed error responses for different failure scenarios
   - Custom exception classes for domain-specific errors

2. **Validation**
   - Could add more comprehensive input validation
   - Consider using Bean Validation (JSR 380) more extensively
   - Add validation for business rules in service layer

3. **Caching**
   - No caching strategy implemented for frequently accessed data
   - Could benefit from Spring Cache for performance optimization
   - Consider caching for search results and place listings

4. **Monitoring and Logging**
   - Could add more comprehensive logging strategy
   - Consider adding metrics collection
   - Implement health check endpoints

5. **Scalability Considerations**
   - Current design uses in-memory H2 database
   - Could benefit from connection pooling configuration
   - Consider adding rate limiting for API endpoints

### Future Enhancements

1. **Performance Optimization**
   - Implement caching for frequently accessed data
   - Add database indexing for common queries
   - Consider pagination for list endpoints

2. **Feature Additions**
   - Add support for file uploads (e.g., teacher CVs, school photos)
   - Implement real-time notifications for lesson bookings
   - Add payment processing integration

3. **Operational Improvements**
   - Add Docker support for containerization
   - Implement CI/CD pipeline
   - Add monitoring and alerting

4. **Security Enhancements**
   - Implement refresh token mechanism
   - Add rate limiting for authentication endpoints
   - Consider adding 2FA support

5. **Documentation**
   - Add more detailed API documentation
   - Include sequence diagrams for complex flows
   - Add deployment guide 

## Environment Configuration

The application supports multiple environments through different property files:

### Development Environment (`application-dev.properties`)
- Default environment (activated by `spring.profiles.active=dev`)
- Uses H2 in-memory database
- Debug logging enabled
- H2 console enabled
- SQL query logging enabled
- JWT token expiration: 24 hours
- Default JWT secret (not for production use)

### Staging Environment (`application-staging.properties`)
- Activated with `spring.profiles.active=staging`
- JWT secret from environment variable with fallback
- Info level logging
- H2 console disabled
- SQL query logging disabled
- JWT token expiration: 1 hour

### Production Environment (`application-prod.properties`)
- Activated with `spring.profiles.active=prod`
- JWT secret must be provided via environment variable
- Warning level logging for security
- H2 console disabled
- SQL query logging disabled
- DDL auto disabled for safety
- JWT token expiration: 1 hour

### Test Environment (`application-test.properties`)
- Automatically activated during tests
- Uses separate H2 test database
- Debug logging enabled
- H2 console enabled
- SQL query logging enabled
- Test-specific JWT configuration
- Runs on port 8081 to avoid conflicts

### Activating Different Environments

1. Using command line:
```bash
java -jar your-app.jar --spring.profiles.active=prod
```

2. Using environment variable:
```bash
export SPRING_PROFILES_ACTIVE=prod
```

3. In `application.properties`:
```properties
spring.profiles.active=dev
```

### Production Deployment

For production deployment:
1. Set a strong JWT secret via environment variable:
```bash
export JWT_SECRET=your-secure-secret-key
```
2. Never commit the actual production JWT secret to version control
3. Consider using a secrets management service
4. Ensure all security features are properly configured
5. Disable development features (H2 console, debug logging, etc.) 

## Git Configuration

The project includes a comprehensive `.gitignore` file that excludes:
- Build output (`target/` directory)
- IDE-specific files (IntelliJ IDEA, Eclipse, VS Code)
- Log files
- Environment variable files
- OS-specific files

### Removing target Directory from Git

If you've accidentally committed the `target/` directory to Git, you can remove it with these commands:

```bash
# Remove target directory from Git tracking (but keep it locally)
git rm -r --cached target/

# Add and commit the changes
git add .gitignore
git commit -m "Remove target directory from Git tracking"

# Push the changes to remove target from remote repository
git push
```

After these steps, the `target/` directory will be ignored by Git, and you can safely delete it locally if needed. It will be recreated when you build the project. 

## Continuous Integration/Continuous Deployment (CI/CD)

The project uses GitHub Actions for automated builds, tests, and code quality checks. The CI/CD pipeline is configured in `.github/workflows/maven.yml` and includes:

### Automated Checks on Every Push and Pull Request

1. **Build and Test**
   - Compiles the code
   - Runs all tests
   - Generates test coverage reports
   - Performs code quality analysis with SpotBugs

2. **Security Checks**
   - Scans for potential secrets in code using Gitleaks
   - Checks for hardcoded credentials
   - Detects API keys and tokens
   - Identifies database connection strings
   - Optional: Dependency vulnerability scanning with Snyk

3. **Artifact Storage**
   - Test results
   - Coverage reports
   - Code quality reports

### Setting Up GitHub Actions

1. **Required Secrets**
   - `GITHUB_TOKEN`: Automatically provided by GitHub
   - `SNYK_TOKEN`: (Optional) Required if using Snyk for dependency scanning

2. **Branch Protection**
   - Enable branch protection rules for the main branch
   - Require status checks to pass before merging
   - Require pull request reviews before merging
   - Include administrators in these restrictions
   - See `.github/branch-protection.md` for detailed setup instructions

3. **Code Owners**
   - Use `.github/CODEOWNERS` to assign review responsibilities
   - Ensures security-related files are reviewed by security team members
   - Automatically assigns reviewers to pull requests

### Pull Request Security

To protect against malicious code in pull requests:

1. **Code Review Process**
   - Always review the full diff of pull requests
   - Pay special attention to changes in security-related files
   - Check for any suspicious patterns or obfuscated code
   - Use the CODEOWNERS file to ensure proper review assignments

2. **Automated Security Checks**
   - The CI pipeline includes Gitleaks to detect potential secrets
   - All pull requests must pass security checks before merging
   - Failed security checks will block merging
   - Gitleaks is configured to redact detected secrets in logs

3. **Secret Management**
   - Never commit secrets to the repository
   - Use GitHub Secrets for sensitive values
   - Rotate any exposed secrets immediately
   - The `.gitleaks.toml` file contains patterns to detect various types of secrets

4. **Limited Permissions**
   - Use the principle of least privilege for GitHub Actions
   - The workflow is configured with minimal required permissions
   - Pull requests from forks run with restricted permissions
   - Concurrency controls prevent parallel workflow runs

### Local Testing of GitHub Actions

To test GitHub Actions workflows locally:

```bash
# Install act (https://github.com/nektos/act)
brew install act

# Run the workflow locally
act -P ubuntu-latest
```

### Pull Request Checks

The following checks must pass before a pull request can be merged:

1. Build success
2. All tests passing
3. No security issues detected by Gitleaks
4. Code review approval from at least one maintainer
5. Optional: No high-severity vulnerabilities in dependencies (if using Snyk)

## Design Assessment

### Strengths

1. **Clean Architecture**
   - Clear separation of concerns with distinct layers (controllers, services, repositories)
   - Well-organized package structure following standard Spring Boot conventions
   - Base service pattern implementation promoting code reuse

2. **Security Implementation**
   - Comprehensive role-based access control (RBAC)
   - JWT-based authentication with proper token validation
   - Method-level security using @PreAuthorize annotations
   - Protected endpoints with appropriate role requirements

3. **API Design**
   - RESTful principles followed consistently
   - Clear endpoint naming and versioning (/api/v1/...)
   - OpenAPI/Swagger documentation for better API discoverability
   - Proper use of HTTP methods and status codes

4. **Testing**
   - Comprehensive integration tests for authentication flows
   - Unit tests for domain models
   - Test coverage reporting with JaCoCo
   - Code quality analysis with SpotBugs

5. **Database Design**
   - Proper entity relationships (User-School-Teacher-Lesson)
   - Appropriate use of JPA annotations
   - Unique constraints on critical fields
   - Foreign key relationships maintaining data integrity
   - Automatic schema generation from entities
   - Performance optimization through indexes

### Areas for Improvement

1. **Error Handling**
   - Could benefit from a centralized exception handling mechanism
   - More detailed error responses for different failure scenarios
   - Custom exception classes for domain-specific errors

2. **Validation**
   - Could add more comprehensive input validation
   - Consider using Bean Validation (JSR 380) more extensively
   - Add validation for business rules in service layer

3. **Caching**
   - No caching strategy implemented for frequently accessed data
   - Could benefit from Spring Cache for performance optimization
   - Consider caching for search results and place listings

4. **Monitoring and Logging**
   - Could add more comprehensive logging strategy
   - Consider adding metrics collection
   - Implement health check endpoints

5. **Scalability Considerations**
   - Current design uses in-memory H2 database
   - Could benefit from connection pooling configuration
   - Consider adding rate limiting for API endpoints

### Future Enhancements

1. **Performance Optimization**
   - Implement caching for frequently accessed data
   - Add database indexing for common queries
   - Consider pagination for list endpoints

2. **Feature Additions**
   - Add support for file uploads (e.g., teacher CVs, school photos)
   - Implement real-time notifications for lesson bookings
   - Add payment processing integration

3. **Operational Improvements**
   - Add Docker support for containerization
   - Implement CI/CD pipeline
   - Add monitoring and alerting

4. **Security Enhancements**
   - Implement refresh token mechanism
   - Add rate limiting for authentication endpoints
   - Consider adding 2FA support

5. **Documentation**
   - Add more detailed API documentation
   - Include sequence diagrams for complex flows
   - Add deployment guide 

## Environment Configuration

The application supports multiple environments through different property files:

### Development Environment (`application-dev.properties`)
- Default environment (activated by `spring.profiles.active=dev`)
- Uses H2 in-memory database
- Debug logging enabled
- H2 console enabled
- SQL query logging enabled
- JWT token expiration: 24 hours
- Default JWT secret (not for production use)

### Staging Environment (`application-staging.properties`)
- Activated with `spring.profiles.active=staging`
- JWT secret from environment variable with fallback
- Info level logging
- H2 console disabled
- SQL query logging disabled
- JWT token expiration: 1 hour

### Production Environment (`application-prod.properties`)
- Activated with `spring.profiles.active=prod`
- JWT secret must be provided via environment variable
- Warning level logging for security
- H2 console disabled
- SQL query logging disabled
- DDL auto disabled for safety
- JWT token expiration: 1 hour

### Test Environment (`application-test.properties`)
- Automatically activated during tests
- Uses separate H2 test database
- Debug logging enabled
- H2 console enabled
- SQL query logging enabled
- Test-specific JWT configuration
- Runs on port 8081 to avoid conflicts

### Activating Different Environments

1. Using command line:
```bash
java -jar your-app.jar --spring.profiles.active=prod
```

2. Using environment variable:
```bash
export SPRING_PROFILES_ACTIVE=prod
```

3. In `application.properties`:
```properties
spring.profiles.active=dev
```

### Production Deployment

For production deployment:
1. Set a strong JWT secret via environment variable:
```bash
export JWT_SECRET=your-secure-secret-key
```
2. Never commit the actual production JWT secret to version control
3. Consider using a secrets management service
4. Ensure all security features are properly configured
5. Disable development features (H2 console, debug logging, etc.) 

## Git Configuration

The project includes a comprehensive `.gitignore` file that excludes:
- Build output (`target/` directory)
- IDE-specific files (IntelliJ IDEA, Eclipse, VS Code)
- Log files
- Environment variable files
- OS-specific files

### Removing target Directory from Git

If you've accidentally committed the `target/` directory to Git, you can remove it with these commands:

```bash
# Remove target directory from Git tracking (but keep it locally)
git rm -r --cached target/

# Add and commit the changes
git add .gitignore
git commit -m "Remove target directory from Git tracking"

# Push the changes to remove target from remote repository
git push
```

After these steps, the `target/` directory will be ignored by Git, and you can safely delete it locally if needed. It will be recreated when you build the project. 

## Continuous Integration/Continuous Deployment (CI/CD)

The project uses GitHub Actions for automated builds, tests, and code quality checks. The CI/CD pipeline is configured in `.github/workflows/maven.yml` and includes:

### Automated Checks on Every Push and Pull Request

1. **Build and Test**
   - Compiles the code
   - Runs all tests
   - Generates test coverage reports
   - Performs code quality analysis with SpotBugs

2. **Security Checks**
   - Scans for potential secrets in code using Gitleaks
   - Checks for hardcoded credentials
   - Detects API keys and tokens
   - Identifies database connection strings
   - Optional: Dependency vulnerability scanning with Snyk

3. **Artifact Storage**
   - Test results
   - Coverage reports
   - Code quality reports

### Setting Up GitHub Actions

1. **Required Secrets**
   - `GITHUB_TOKEN`: Automatically provided by GitHub
   - `SNYK_TOKEN`: (Optional) Required if using Snyk for dependency scanning

2. **Branch Protection**
   - Enable branch protection rules for the main branch
   - Require status checks to pass before merging
   - Require pull request reviews before merging
   - Include administrators in these restrictions
   - See `.github/branch-protection.md` for detailed setup instructions

3. **Code Owners**
   - Use `.github/CODEOWNERS` to assign review responsibilities
   - Ensures security-related files are reviewed by security team members
   - Automatically assigns reviewers to pull requests

### Pull Request Security

To protect against malicious code in pull requests:

1. **Code Review Process**
   - Always review the full diff of pull requests
   - Pay special attention to changes in security-related files
   - Check for any suspicious patterns or obfuscated code
   - Use the CODEOWNERS file to ensure proper review assignments

2. **Automated Security Checks**
   - The CI pipeline includes Gitleaks to detect potential secrets
   - All pull requests must pass security checks before merging
   - Failed security checks will block merging
   - Gitleaks is configured to redact detected secrets in logs

3. **Secret Management**
   - Never commit secrets to the repository
   - Use GitHub Secrets for sensitive values
   - Rotate any exposed secrets immediately
   - The `.gitleaks.toml` file contains patterns to detect various types of secrets

4. **Limited Permissions**
   - Use the principle of least privilege for GitHub Actions
   - The workflow is configured with minimal required permissions
   - Pull requests from forks run with restricted permissions
   - Concurrency controls prevent parallel workflow runs

### Local Testing of GitHub Actions

To test GitHub Actions workflows locally:

```bash
# Install act (https://github.com/nektos/act)
brew install act

# Run the workflow locally
act -P ubuntu-latest
```

### Pull Request Checks

The following checks must pass before a pull request can be merged:

1. Build success
2. All tests passing
3. No security issues detected by Gitleaks
4. Code review approval from at least one maintainer
5. Optional: No high-severity vulnerabilities in dependencies (if using Snyk) 