package com.skiconnect.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class LessonAvailabilityTest {

    @Test
    void testLessonAvailabilityCreation() {
        LessonAvailability availability = new LessonAvailability();
        LocalDateTime now = LocalDateTime.now();
        availability.setStartTime(now);
        availability.setEndTime(now.plusHours(1));

        assertNotNull(availability);
        assertEquals(now, availability.getStartTime());
        assertEquals(now.plusHours(1), availability.getEndTime());
        assertTrue(availability.isAvailable());
    }

    @Test
    void testLessonAvailabilityWithTeacher() {
        LessonAvailability availability = new LessonAvailability();
        LocalDateTime now = LocalDateTime.now();
        availability.setStartTime(now);
        availability.setEndTime(now.plusHours(1));

        Teacher teacher = new Teacher();
        teacher.setName("Test Teacher");
        availability.setTeacher(teacher);

        assertNotNull(availability.getTeacher());
        assertEquals("Test Teacher", availability.getTeacher().getName());
    }

    @Test
    void testLessonAvailabilityWithSchool() {
        LessonAvailability availability = new LessonAvailability();
        LocalDateTime now = LocalDateTime.now();
        availability.setStartTime(now);
        availability.setEndTime(now.plusHours(1));

        School school = new School();
        school.setName("Test School");
        availability.setSchool(school);

        assertNotNull(availability.getSchool());
        assertEquals("Test School", availability.getSchool().getName());
    }

    @Test
    void testLessonAvailabilityWithPlace() {
        LessonAvailability availability = new LessonAvailability();
        LocalDateTime now = LocalDateTime.now();
        availability.setStartTime(now);
        availability.setEndTime(now.plusHours(1));

        Place place = new Place();
        place.setName("Test Place");
        availability.setPlace(place);

        assertNotNull(availability.getPlace());
        assertEquals("Test Place", availability.getPlace().getName());
    }

    @Test
    void testLessonAvailabilityWithStudent() {
        LessonAvailability availability = new LessonAvailability();
        LocalDateTime now = LocalDateTime.now();
        availability.setStartTime(now);
        availability.setEndTime(now.plusHours(1));

        User student = new User();
        student.setUsername("student");
        availability.setStudent(student);

        assertNotNull(availability.getStudent());
        assertEquals("student", availability.getStudent().getUsername());
    }

    @Test
    void testLessonAvailabilityBooking() {
        LessonAvailability availability = new LessonAvailability();
        LocalDateTime now = LocalDateTime.now();
        availability.setStartTime(now);
        availability.setEndTime(now.plusHours(1));

        User student = new User();
        student.setUsername("student");
        availability.setStudent(student);
        availability.setAvailable(false);

        assertFalse(availability.isAvailable());
        assertNotNull(availability.getStudent());
        assertEquals("student", availability.getStudent().getUsername());
    }

    @Test
    void testLessonAvailabilityWithFreelancer() {
        LessonAvailability availability = new LessonAvailability();
        LocalDateTime now = LocalDateTime.now();
        availability.setStartTime(now);
        availability.setEndTime(now.plusHours(1));

        School school = new School();
        school.setName("Freelancer School");
        school.setTeachers(new ArrayList<>());

        Teacher teacher = new Teacher();
        teacher.setName("Freelancer");
        teacher.setFreelancer(true);
        teacher.setSchool(school);
        school.getTeachers().add(teacher);

        availability.setTeacher(teacher);
        availability.setSchool(school);

        assertTrue(availability.getTeacher().isFreelancer());
        assertEquals("Freelancer School", availability.getTeacher().getSchool().getName());
    }
} 