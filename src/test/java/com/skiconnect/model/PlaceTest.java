package com.skiconnect.model;

import com.skiconnect.BaseTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlaceTest extends BaseTest {

    @Test
    void testPlaceCreation() {
        Place place = new Place();
        place.setName("Test Place");
        place.setLocation("Test Location");

        assertNotNull(place);
        assertEquals("Test Place", place.getName());
        assertEquals("Test Location", place.getLocation());
        assertNotNull(place.getSchools());
        assertNotNull(place.getLessonAvailabilities());
    }

    @Test
    void testPlaceWithSchools() {
        Place place = new Place();
        place.setName("Test Place");
        place.setLocation("Test Location");

        School school1 = new School();
        school1.setName("School 1");
        school1.setPlace(place);

        School school2 = new School();
        school2.setName("School 2");
        school2.setPlace(place);

        List<School> schools = new ArrayList<>();
        schools.add(school1);
        schools.add(school2);
        place.setSchools(schools);

        assertNotNull(place.getSchools());
        assertEquals(2, place.getSchools().size());
        assertTrue(place.getSchools().stream()
                .anyMatch(s -> s.getName().equals("School 1")));
        assertTrue(place.getSchools().stream()
                .anyMatch(s -> s.getName().equals("School 2")));
    }

    @Test
    void testPlaceWithLessonAvailabilities() {
        Place place = new Place();
        place.setName("Test Place");
        place.setLocation("Test Location");

        LessonAvailability availability = new LessonAvailability();
        availability.setPlace(place);
        availability.setStartTime(java.time.LocalDateTime.now());
        availability.setEndTime(java.time.LocalDateTime.now().plusHours(1));

        List<LessonAvailability> availabilities = new ArrayList<>();
        availabilities.add(availability);
        place.setLessonAvailabilities(availabilities);

        assertNotNull(place.getLessonAvailabilities());
        assertEquals(1, place.getLessonAvailabilities().size());
    }

    @Test
    void testPlaceWithSchoolAndTeacher() {
        Place place = new Place();
        place.setName("Test Place");
        place.setLocation("Test Location");

        School school = new School();
        school.setName("Test School");
        school.setPlace(place);
        school.setTeachers(new ArrayList<>());

        Teacher teacher = new Teacher();
        teacher.setName("Test Teacher");
        teacher.setSchool(school);
        school.getTeachers().add(teacher);

        List<School> schools = new ArrayList<>();
        schools.add(school);
        place.setSchools(schools);

        assertNotNull(place.getSchools());
        assertEquals(1, place.getSchools().size());
        assertEquals(1, place.getSchools().get(0).getTeachers().size());
        assertEquals("Test Teacher", place.getSchools().get(0).getTeachers().get(0).getName());
    }
} 