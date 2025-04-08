package com.skiconnect.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserTest {

    @Test
    void testUserCreation() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);

        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("test@example.com", user.getEmail());
        assertTrue(user.getRoles().contains("ROLE_USER"));
    }

    @Test
    void testUserDetailsImplementation() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);

        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
        
        assertEquals(1, user.getAuthorities().size());
        assertTrue(user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testUserWithSchool() {
        User user = new User();
        user.setUsername("schooluser");
        user.setPassword("password");
        user.setEmail("school@example.com");
        
        School school = new School();
        school.setName("Test School");
        school.setUser(user);
        
        user.setSchool(school);

        assertNotNull(user.getSchool());
        assertEquals("Test School", user.getSchool().getName());
    }

    @Test
    void testUserWithTeacher() {
        User user = new User();
        user.setUsername("teacheruser");
        user.setPassword("password");
        user.setEmail("teacher@example.com");
        
        Teacher teacher = new Teacher();
        teacher.setName("Test Teacher");
        teacher.setUser(user);
        teacher.setFreelancer(true);
        
        user.setTeacher(teacher);

        assertNotNull(user.getTeacher());
        assertEquals("Test Teacher", user.getTeacher().getName());
        assertTrue(user.getTeacher().isFreelancer());
    }
} 