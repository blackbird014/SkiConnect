package com.skiconnect.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SchoolTest {

    @Test
    void testSchoolCreation() {
        School school = new School();
        school.setName("Test School");

        assertNotNull(school);
        assertEquals("Test School", school.getName());
        assertNotNull(school.getTeachers());
        assertNotNull(school.getLessonAvailabilities());
    }

    @Test
    void testSchoolWithUser() {
        School school = new School();
        school.setName("Test School");

        User user = new User();
        user.setUsername("schooluser");
        user.setPassword("password");
        user.setEmail("school@example.com");
        
        school.setUser(user);

        assertNotNull(school.getUser());
        assertEquals("schooluser", school.getUser().getUsername());
    }

    @Test
    void testSchoolWithTeachers() {
        School school = new School();
        school.setName("Test School");

        Teacher teacher1 = new Teacher();
        teacher1.setName("Teacher 1");
        teacher1.setSchool(school);

        Teacher teacher2 = new Teacher();
        teacher2.setName("Teacher 2");
        teacher2.setSchool(school);

        List<Teacher> teachers = new ArrayList<>();
        teachers.add(teacher1);
        teachers.add(teacher2);
        school.setTeachers(teachers);

        assertNotNull(school.getTeachers());
        assertEquals(2, school.getTeachers().size());
        assertTrue(school.getTeachers().stream()
                .anyMatch(t -> t.getName().equals("Teacher 1")));
        assertTrue(school.getTeachers().stream()
                .anyMatch(t -> t.getName().equals("Teacher 2")));
    }

    @Test
    void testSchoolWithPlace() {
        School school = new School();
        school.setName("Test School");

        Place place = new Place();
        place.setName("Test Place");
        place.setLocation("Test Location");
        
        school.setPlace(place);

        assertNotNull(school.getPlace());
        assertEquals("Test Place", school.getPlace().getName());
        assertEquals("Test Location", school.getPlace().getLocation());
    }

    @Test
    void testSchoolWithLessonAvailabilities() {
        School school = new School();
        school.setName("Test School");

        LessonAvailability availability = new LessonAvailability();
        availability.setSchool(school);
        availability.setStartTime(java.time.LocalDateTime.now());
        availability.setEndTime(java.time.LocalDateTime.now().plusHours(1));

        List<LessonAvailability> availabilities = new ArrayList<>();
        availabilities.add(availability);
        school.setLessonAvailabilities(availabilities);

        assertNotNull(school.getLessonAvailabilities());
        assertEquals(1, school.getLessonAvailabilities().size());
    }
} 