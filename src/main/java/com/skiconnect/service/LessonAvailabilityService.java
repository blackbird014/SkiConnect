package com.skiconnect.service;

import com.skiconnect.model.LessonAvailability;

import java.time.LocalDateTime;
import java.util.List;

public interface LessonAvailabilityService extends BaseService<LessonAvailability, Long> {
    List<LessonAvailability> findByTeacherId(Long teacherId);
    List<LessonAvailability> findBySchoolId(Long schoolId);
    List<LessonAvailability> findByPlaceId(Long placeId);
    List<LessonAvailability> findByStudentId(Long studentId);
    List<LessonAvailability> findByIsAvailable(boolean isAvailable);
    List<LessonAvailability> findAvailableLessonsByPlaceAndTimeRange(Long placeId, LocalDateTime startTime, LocalDateTime endTime);
    List<LessonAvailability> findAvailableLessonsBySchoolAndTimeRange(Long schoolId, LocalDateTime startTime, LocalDateTime endTime);
    List<LessonAvailability> findAvailableLessonsByTeacherAndTimeRange(Long teacherId, LocalDateTime startTime, LocalDateTime endTime);
    LessonAvailability createAvailability(Long teacherId, Long schoolId, Long placeId, LocalDateTime startTime, LocalDateTime endTime);
    LessonAvailability bookLesson(Long availabilityId, Long studentId);
    LessonAvailability cancelBooking(Long availabilityId);
} 