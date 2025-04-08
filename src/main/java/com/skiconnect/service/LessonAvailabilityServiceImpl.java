package com.skiconnect.service;

import com.skiconnect.model.LessonAvailability;
import com.skiconnect.model.Place;
import com.skiconnect.model.School;
import com.skiconnect.model.Teacher;
import com.skiconnect.model.User;
import com.skiconnect.repository.LessonAvailabilityRepository;
import com.skiconnect.repository.PlaceRepository;
import com.skiconnect.repository.SchoolRepository;
import com.skiconnect.repository.TeacherRepository;
import com.skiconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class LessonAvailabilityServiceImpl extends BaseServiceImpl<LessonAvailability, Long> implements LessonAvailabilityService {

    private final LessonAvailabilityRepository lessonAvailabilityRepository;
    private final TeacherRepository teacherRepository;
    private final SchoolRepository schoolRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;

    @Autowired
    public LessonAvailabilityServiceImpl(LessonAvailabilityRepository lessonAvailabilityRepository,
                                       TeacherRepository teacherRepository,
                                       SchoolRepository schoolRepository,
                                       PlaceRepository placeRepository,
                                       UserRepository userRepository) {
        super(lessonAvailabilityRepository);
        this.lessonAvailabilityRepository = lessonAvailabilityRepository;
        this.teacherRepository = teacherRepository;
        this.schoolRepository = schoolRepository;
        this.placeRepository = placeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<LessonAvailability> findByTeacherId(Long teacherId) {
        return lessonAvailabilityRepository.findByTeacherId(teacherId);
    }

    @Override
    public List<LessonAvailability> findBySchoolId(Long schoolId) {
        return lessonAvailabilityRepository.findBySchoolId(schoolId);
    }

    @Override
    public List<LessonAvailability> findByPlaceId(Long placeId) {
        return lessonAvailabilityRepository.findByPlaceId(placeId);
    }

    @Override
    public List<LessonAvailability> findByStudentId(Long studentId) {
        return lessonAvailabilityRepository.findByStudentId(studentId);
    }

    @Override
    public List<LessonAvailability> findByIsAvailable(boolean isAvailable) {
        return lessonAvailabilityRepository.findByIsAvailable(isAvailable);
    }

    @Override
    public List<LessonAvailability> findAvailableLessonsByPlaceAndTimeRange(Long placeId, LocalDateTime startTime, LocalDateTime endTime) {
        return lessonAvailabilityRepository.findAvailableLessonsByPlaceAndTimeRange(placeId, startTime, endTime);
    }

    @Override
    public List<LessonAvailability> findAvailableLessonsBySchoolAndTimeRange(Long schoolId, LocalDateTime startTime, LocalDateTime endTime) {
        return lessonAvailabilityRepository.findAvailableLessonsBySchoolAndTimeRange(schoolId, startTime, endTime);
    }

    @Override
    public List<LessonAvailability> findAvailableLessonsByTeacherAndTimeRange(Long teacherId, LocalDateTime startTime, LocalDateTime endTime) {
        return lessonAvailabilityRepository.findAvailableLessonsByTeacherAndTimeRange(teacherId, startTime, endTime);
    }

    @Override
    public LessonAvailability createAvailability(Long teacherId, Long schoolId, Long placeId, LocalDateTime startTime, LocalDateTime endTime) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with id: " + teacherId));
        
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalArgumentException("School not found with id: " + schoolId));
        
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("Place not found with id: " + placeId));

        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        LessonAvailability availability = new LessonAvailability();
        availability.setTeacher(teacher);
        availability.setSchool(school);
        availability.setPlace(place);
        availability.setStartTime(startTime);
        availability.setEndTime(endTime);
        availability.setAvailable(true);

        return save(availability);
    }

    @Override
    public LessonAvailability bookLesson(Long availabilityId, Long studentId) {
        LessonAvailability availability = findById(availabilityId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson availability not found with id: " + availabilityId));

        if (!availability.isAvailable()) {
            throw new IllegalStateException("Lesson is not available for booking");
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + studentId));

        availability.setStudent(student);
        availability.setAvailable(false);

        return save(availability);
    }

    @Override
    public LessonAvailability cancelBooking(Long availabilityId) {
        LessonAvailability availability = findById(availabilityId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson availability not found with id: " + availabilityId));

        if (availability.isAvailable()) {
            throw new IllegalStateException("Lesson is not booked");
        }

        availability.setStudent(null);
        availability.setAvailable(true);

        return save(availability);
    }
} 