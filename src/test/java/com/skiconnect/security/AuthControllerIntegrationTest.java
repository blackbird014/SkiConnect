package com.skiconnect.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skiconnect.model.User;
import com.skiconnect.repository.UserRepository;
import com.skiconnect.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import com.skiconnect.model.Place;
import com.skiconnect.model.School;
import com.skiconnect.model.Teacher;
import com.skiconnect.repository.PlaceRepository;
import com.skiconnect.repository.SchoolRepository;
import com.skiconnect.repository.TeacherRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Rollback transactions after each test
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @BeforeEach
    void setUp() {
        // Clean up potentially existing users from previous test runs if needed,
        // although @Transactional should handle this.
        // userRepository.deleteAll();
    }

    // --- Signup Tests --- 

    @Test
    void testSignupSuccess() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("testuser");
        signupRequest.setName("Test User");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password123");
        Set<String> roles = new HashSet<>();
        roles.add("ski_teacher");
        signupRequest.setRole(roles);

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully!"));
        
        User user = userRepository.findByUsername("testuser").orElseThrow();
        assert(user.getName().equals("Test User"));
        assert(passwordEncoder.matches("password123", user.getPassword()));
        assert(user.getRoles().contains("ROLE_SKI_TEACHER"));
    }

    @Test
    void testSignupUsernameExists() throws Exception {
        // Arrange: Create existing user
        userService.createUser("existinguser", "Existing Name", "password", "unique@example.com", Set.of("ROLE_USER"));

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("existinguser");
        signupRequest.setName("New Name");
        signupRequest.setEmail("new@example.com");
        signupRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Username is already taken!"));
    }

    @Test
    void testSignupEmailExists() throws Exception {
         // Arrange: Create existing user
        userService.createUser("newuser", "New User Name", "password", "existing@example.com", Set.of("ROLE_USER"));

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("anotheruser"); 
        signupRequest.setName("Another Name");
        signupRequest.setEmail("existing@example.com");
        signupRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Email is already in use!"));
    }

    // --- Login Tests --- 

    @Test
    void testLoginSuccess() throws Exception {
        // Arrange: Create a user first
        userService.createUser("loginuser", "Login User", "password123", "login@example.com", Set.of("ROLE_SKI_SCHOOL"));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("loginuser");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value("loginuser"))
                .andExpect(jsonPath("$.email").value("login@example.com"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_SKI_SCHOOL"));
    }

    @Test
    void testLoginFailure() throws Exception {
        userService.createUser("loginuser2", "Login User 2", "password123", "login2@example.com", Set.of("ROLE_USER"));
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("loginuser2");
        loginRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden()); // Changed expectation to 403
    }

    // --- Access Control Tests --- 

     @Test
     void testAccessProtectedEndpointUnauthenticated() throws Exception {
         mockMvc.perform(get("/api/v1/lessons/1"))
                 .andExpect(status().isForbidden());
     }

    @Test
    void testAccessProtectedEndpointAuthenticated() throws Exception {
        userService.createUser("authtestuser", "Auth Test User", "password123", "auth@example.com", Set.of("ROLE_SKI_TEACHER"));
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("authtestuser");
        loginRequest.setPassword("password123");
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
        String responseString = result.getResponse().getContentAsString();
        JwtResponse jwtResponse = objectMapper.readValue(responseString, JwtResponse.class);
        String token = jwtResponse.getToken();

        mockMvc.perform(get("/api/v1/lessons/1")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAccessProtectedEndpointWrongRole() throws Exception {
        userService.createUser("userroleuser", "User Role User", "password123", "user@example.com", Set.of("ROLE_USER"));
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("userroleuser");
        loginRequest.setPassword("password123");
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
        String responseString = result.getResponse().getContentAsString();
        JwtResponse jwtResponse = objectMapper.readValue(responseString, JwtResponse.class);
        String token = jwtResponse.getToken();
        String lessonJson = "{\"school_id\": 1, \"place_id\": 1, \"date\": \"2025-01-01T10:00:00\", \"status\": \"AVAILABLE\", \"duration_minutes\": 60}"; // Simplified example

        mockMvc.perform(post("/api/v1/lessons")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(lessonJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAccessProtectedEndpointCorrectRole() throws Exception {
        // Create a user with SKI_SCHOOL role
        User schoolUser = userService.createUser("schoolroleuser", "School Role User", "password123", "school@example.com", Set.of("ROLE_SKI_SCHOOL"));
        
        // Create required entities
        Place place = new Place();
        place.setName("Test Resort");
        place.setLocation("Test Location");
        place = placeRepository.save(place);

        School school = new School();
        school.setName("Test School");
        school.setPlace(place);
        school.setUser(schoolUser);
        school = schoolRepository.save(school);

        Teacher teacher = new Teacher();
        teacher.setName("Test Teacher");
        teacher.setSchool(school);
        teacher.setUser(schoolUser);
        teacher = teacherRepository.save(teacher);

        // Login to get JWT token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("schoolroleuser");
        loginRequest.setPassword("password123");
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
        
        JwtResponse jwtResponse = objectMapper.readValue(result.getResponse().getContentAsString(), JwtResponse.class);
        String token = jwtResponse.getToken();

        // Create lesson availability with proper entity IDs
        String lessonJson = String.format(
            "{\"schoolId\": %d, \"teacherId\": %d, \"placeId\": %d, \"date\": \"2025-01-01T10:00:00\", \"durationMinutes\": 60}",
            school.getId(),
            teacher.getId(),
            place.getId()
        );

        mockMvc.perform(post("/api/v1/lessons")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(lessonJson))
                .andExpect(status().isCreated());
    }
} 