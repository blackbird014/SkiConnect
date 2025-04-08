#!/bin/bash



# Create directory structure
mkdir -p src/main/java/com/skiconnect/controller
mkdir -p src/main/java/com/skiconnect/model
mkdir -p src/main/java/com/skiconnect/repository
mkdir -p src/main/resources/db
mkdir -p src/main/resources/api

# Create Java controller files with comments
echo "// Controller for managing places (e.g., POST /places)" > src/main/java/com/skiconnect/controller/PlaceController.java
echo "// Controller for managing users and roles (e.g., POST /users, POST /users/{userId}/roles)" > src/main/java/com/skiconnect/controller/UserController.java
echo "// Controller for managing lessons (e.g., POST /lessons, GET /lessons)" > src/main/java/com/skiconnect/controller/LessonController.java
echo "// Controller for search functionality (e.g., GET /search)" > src/main/java/com/skiconnect/controller/SearchController.java

# Create Java model files with comments
echo "// Model for Place entity (id, name, location)" > src/main/java/com/skiconnect/model/Place.java
echo "// Model for User entity (id, name, email, cv, roles)" > src/main/java/com/skiconnect/model/User.java
echo "// Model for Role entity (id, name)" > src/main/java/com/skiconnect/model/Role.java
echo "// Model for Lesson entity (id, school_id, teacher_id, place_id, student_id, date, status, duration_minutes)" > src/main/java/com/skiconnect/model/Lesson.java
echo "// Model for SearchResponse (schools, teachers, totals)" > src/main/java/com/skiconnect/model/SearchResponse.java

# Create Java repository file with comment
echo "// JPA repository for User entity (e.g., findByEmail, save)" > src/main/java/com/skiconnect/repository/UserRepository.java

# Create DB schema file (schema.sql)
cat << 'EOF' > src/main/resources/db/schema.sql
-- Database schema for SkiConnect
CREATE TABLE users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  cv TEXT
);

CREATE TABLE roles (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE user_roles (
  user_id INT,
  role_id INT,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (role_id) REFERENCES roles(id),
  PRIMARY KEY (user_id, role_id)
);

CREATE TABLE places (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  location VARCHAR(255) NOT NULL
);

CREATE TABLE lessons (
  id INT PRIMARY KEY AUTO_INCREMENT,
  school_id INT NOT NULL,
  teacher_id INT,
  place_id INT NOT NULL,
  student_id INT,
  date TIMESTAMP NOT NULL,
  status VARCHAR(50) NOT NULL,
  duration_minutes INT NOT NULL,
  FOREIGN KEY (school_id) REFERENCES users(id),
  FOREIGN KEY (teacher_id) REFERENCES users(id),
  FOREIGN KEY (place_id) REFERENCES places(id),
  FOREIGN KEY (student_id) REFERENCES users(id)
);
EOF

# Create DB prefill file (data.sql)
cat << 'EOF' > src/main/resources/db/data.sql
-- Prefill data for SkiConnect
INSERT INTO roles (id, name) VALUES
(1, 'admin'),
(2, 'ski_school'),
(3, 'ski_teacher'),
(4, 'student_group'),
(5, 'student');

INSERT INTO users (id, name, email, cv) VALUES
(1, 'Admin User', 'admin@example.com', NULL),
(2, 'Peak Ski School', 'peak@example.com', '{"experience": "10 years in operation", "qualifications": "Certified by Ski Federation", "bio": "Top school in the region"}'),
(3, 'John Freelancer', 'john@example.com', '{"experience": "5 years teaching", "qualifications": "Level 2 Ski Instructor", "bio": "Passionate about teaching beginners"}'),
(4, 'Jane Student', 'jane@example.com', NULL);

INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1),
(2, 2),
(3, 2),
(3, 3),
(4, 5);

INSERT INTO places (id, name, location) VALUES
(1, 'Snowpeak Resort', 'North Slope');

INSERT INTO lessons (id, school_id, teacher_id, place_id, student_id, date, status, duration_minutes) VALUES
(1, 2, NULL, 1, NULL, '2025-04-10T10:00:00Z', 'available', 60),
(2, 3, 3, 1, NULL, '2025-04-10T12:00:00Z', 'available', 60);
EOF

# Create OpenAPI YAML file (skiconnect-api.yaml)
cat << 'EOF' > src/main/resources/api/skiconnect-api.yaml
openapi: 3.0.3
info:
  title: SkiConnect API
  description: API for connecting ski teachers with students, with role-based access and place-based search.
  version: 1.0.0
servers:
  - url: http://<host>:<port>/api/v1
paths:
  /places:
    post:
      summary: Create a new place (admin only)
      operationId: createPlace
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/PlaceRequest'
      responses:
        '201':
          description: Place created
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PlaceResponse'
  /users:
    post:
      summary: Create a new user with optional CV (admin only)
      operationId: createUser
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/UserRequest'
      responses:
        '201':
          description: User created
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/UserResponse'
  /users/{userId}/roles:
    post:
      summary: Assign a role to a user (admin only)
      operationId: assignRole
      parameters:
        - name: userId
          in: path
          required: true
          schema:
            type: integer
            format: int32
      requestBody:
        required: true  content:
            application/json:
              schema:
                $ref: '#/components/schemas/RoleAssignmentRequest'
      responses:
        '200':
          description: Role assigned
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/UserRolesResponse'
  /lessons:
    post:
      summary: Create lesson availability (ski_school only)
      operationId: createLesson
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/LessonRequest'
      responses:
        '201':
          description: Lesson created
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/LessonResponse'
    get:
      summary: List lessons (filtered by role in future)
      operationId: listLessons
      parameters:
        - name: school_id
          in: query
          required: false
          schema:
            type: integer
            format: int32
        - name: teacher_id
          in: query
          required: false
          schema:
            type: integer
            format: int32
        - name: place_id
          in: query
          required: false
          schema:
            type: integer
            format: int32
        - name: status
          in: query
          required: false
          schema:
            type: string
            enum: [available, booked]
      responses:
        '200':
          description: List of lessons
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/LessonListResponse'
  /lessons/{lessonId}/book:
    post:
      summary: Book a lesson (student/student_group)
      operationId: bookLesson
      parameters:
        - name: lessonId
          in: path
          required: true
          schema:
            type: integer
            format: int32
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/BookLessonRequest'
      responses:
        '200':
          description: Lesson booked
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/LessonResponse'
  /search:
    get:
      summary: Search schools and teachers by place
      operationId: searchByPlace
      parameters:
        - name: place_id
          in: query
          required: true
          schema:
            type: integer
            format: int32
        - name: type
          in: query
          required: false
          schema:
            type: string
            enum: [schools, teachers]
      responses:
        '200':
          description: Search results with CVs
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/SearchResponse'
components:
  schemas:
    PlaceRequest:
      type: object
      required: [name, location]
      properties:
        name:
          type: string
          example: "Snowpeak Resort"
        location:
          type: string
          example: "North Slope"
    PlaceResponse:
      type: object
      properties:
        id:
          type: integer
          format: int32
          example: 1
        name:
          type: string
          example: "Snowpeak Resort"
        location:
          type: string
          example: "North Slope"
    UserRequest:
      type: object
      required: [name, email]
      properties:
        name:
          type: string
          example: "John Doe"
        email:
          type: string
          example: "john@example.com"
        cv:
          type: object
          properties:
            experience:
              type: string
              example: "5 years teaching at Snowpeak"
            qualifications:
              type: string
              example: "Level 2 Ski Instructor"
            bio:
              type: string
              example: "Passionate about teaching beginners"
    UserResponse:
      type: object
      properties:
        id:
          type: integer
          format: int32
          example: 1
        name:
          type: string
          example: "John Doe"
        email:
          type: string
          example: "john@example.com"
        cv:
          $ref: '#/components/schemas/CV'
    RoleAssignmentRequest:
      type: object
      required: [role_id]
      properties:
        role_id:
          type: integer
          format: int32
          example: 1
    UserRolesResponse:
      type: object
      properties:
        user_id:
          type: integer
          format: int32
          example: 1
        roles:
          type: array
          items:
            type: object
            properties:
              id:
                type: integer
                format: int32
                example: 1
              name:
                type: string
                example: "ski_teacher"
    LessonRequest:
      type: object
      required: [school_id, place_id, date, duration_minutes]
      properties:
        school_id:
          type: integer
          format: int32
          example: 1
        teacher_id:
          type: integer
          format: int32
          nullable: true
          example: null
        place_id:
          type: integer
          format: int32
          example: 1
        date:
          type: string
          format: date-time
          example: "2025-04-10T10:00:00Z"
        duration_minutes:
          type: integer
          format: int32
          example: 60
    LessonResponse:
      type: object
      properties:
        id:
          type: integer
          format: int32
          example: 1
        school_id:
          type: integer
          format: int32
          example: 1
        teacher_id:
          type: integer
          format: int32
          nullable: true
          example: null
        place_id:
          type: integer
          format: int32
          example: 1
        student_id:
          type: integer
          format: int32
          nullable: true
          example: null
        date:
          type: string
          format: date-time
          example: "2025-04-10T10:00:00Z"
        status:
          type: string
          enum: [available, booked]
          example: "available"
        duration_minutes:
          type: integer
          format: int32
          example: 60
    LessonListResponse:
      type: object
      properties:
        lessons:
          type: array
          items:
            $ref: '#/components/schemas/LessonResponse'
        total:
          type: integer
          format: int32
          example: 1
    BookLessonRequest:
      type: object
      required: [student_id]
      properties:
        student_id:
          type: integer
          format: int32
          example: 3
    SearchResponse:
      type: object
      properties:
        schools:
          type: array
          items:
            type: object
            properties:
              id:
                type: integer
                format: int32
                example: 1
              name:
                type: string
                example: "Peak Ski School"
              email:
                type: string
                example: "peak@example.com"
              cv:
                $ref: '#/components/schemas/CV'
              available_lessons:
                type: integer
                format: int32
                example: 3
        teachers:
          type: array
          items:
            type: object
            properties:
              id:
                type: integer
                format: int32
                example: 2
              name:
                type: string
                example: "John Doe"
              email:
                type: string
                example: "john@example.com"
              cv:
                $ref: '#/components/schemas/CV'
              available_lessons:
                type: integer
                format: int32
                example: 1
              school_id:
                type: integer
                format: int32
                example: 1
        total_schools:
          type: integer
          format: int32
          example: 1
        total_teachers:
          type: integer
          format: int32
          example: 1
    CV:
      type: object
      properties:
        experience:
          type: string
          example: "5 years teaching at Snowpeak"
        qualifications:
          type: string
          example: "Level 2 Ski Instructor"
        bio:
          type: string
          example: "Passionate about teaching beginners"
EOF

# Create application.properties
echo "# Spring Boot configuration" > src/main/resources/application.properties
echo "spring.datasource.url=jdbc:h2:mem:skiconnect;DB_CLOSE_DELAY=-1" >> src/main/resources/application.properties
echo "spring.datasource.driverClassName=org.h2.Driver" >> src/main/resources/application.properties
echo "spring.sql.init.mode=always" >> src/main/resources/application.properties

# Create pom.xml (basic Maven setup)
cat << 'EOF' > pom.xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.skiconnect</groupId>
  <artifactId>skiconnect</artifactId>
  <version>1.0-SNAPSHOT</version>
  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
  </parent>
  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
      <groupId>com.h2database</groupId>
      <artifactId>h2</artifactId>
      <scope>runtime</scope>
    </dependency>
  </dependencies>
  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>
</project>
EOF

# Make the script executable
chmod +x generate.sh

echo "Project structure for $PROJECT_NAME created successfully!"