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