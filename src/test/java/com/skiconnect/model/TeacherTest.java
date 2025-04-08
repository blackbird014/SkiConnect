package com.skiconnect.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TeacherTest {

    @Test
    void testTeacherCreation() {
        Teacher teacher = new Teacher();
        teacher.setName("Test Teacher");

        assertNotNull(teacher);
        assertEquals("Test Teacher", teacher.getName());
        assertFalse(teacher.isFreelancer());
        assertNotNull(teacher.getLessonAvailabilities());
    }

    @Test
    void testTeacherWithUser() {
        Teacher teacher = new Teacher();
        teacher.setName("Test Teacher");

        User user = new User();
        user.setUsername("teacheruser");
        user.setPassword("password");
        user.setEmail("teacher@example.com");
        
        teacher.setUser(user);

        assertNotNull(teacher.getUser());
        assertEquals("teacheruser", teacher.getUser().getUsername());
    }

    @Test
    void testTeacherWithSchool() {
        Teacher teacher = new Teacher();
        teacher.setName("Test Teacher");

        School school = new School();
        school.setName("Test School");
        
        teacher.setSchool(school);

        assertNotNull(teacher.getSchool());
        assertEquals("Test School", teacher.getSchool().getName());
    }

    @Test
    void testFreelancerTeacher() {
        Teacher teacher = new Teacher();
        teacher.setName("Test Teacher");
        teacher.setFreelancer(true);

        assertTrue(teacher.isFreelancer());
    }

    @Test
    void testTeacherWithLessonAvailabilities() {
        Teacher teacher = new Teacher();
        teacher.setName("Test Teacher");

        LessonAvailability availability = new LessonAvailability();
        availability.setTeacher(teacher);
        availability.setStartTime(java.time.LocalDateTime.now());
        availability.setEndTime(java.time.LocalDateTime.now().plusHours(1));

        List<LessonAvailability> availabilities = new ArrayList<>();
        availabilities.add(availability);
        teacher.setLessonAvailabilities(availabilities);

        assertNotNull(teacher.getLessonAvailabilities());
        assertEquals(1, teacher.getLessonAvailabilities().size());
    }

    @Test
    void testFreelancerTeacherAsSchool() {
        Teacher teacher = new Teacher();
        teacher.setName("Test Teacher");
        teacher.setFreelancer(true);

        User user = new User();
        user.setUsername("freelancer");
        user.setPassword("password");
        user.setEmail("freelancer@example.com");
        
        teacher.setUser(user);
        teacher.setSchool(new School()); // Freelancer is also a school
        teacher.getSchool().setUser(user);
        teacher.getSchool().setName("Freelancer School");

        assertTrue(teacher.isFreelancer());
        assertNotNull(teacher.getSchool());
        assertEquals(teacher.getUser(), teacher.getSchool().getUser());
    }
} 