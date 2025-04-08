package com.skiconnect.repository;

import com.skiconnect.model.LessonAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LessonAvailabilityRepository extends JpaRepository<LessonAvailability, Long> {
    List<LessonAvailability> findByTeacherId(Long teacherId);
    List<LessonAvailability> findBySchoolId(Long schoolId);
    List<LessonAvailability> findByPlaceId(Long placeId);
    List<LessonAvailability> findByStudentId(Long studentId);
    List<LessonAvailability> findByIsAvailable(boolean isAvailable);
    
    @Query("SELECT la FROM LessonAvailability la WHERE la.place.id = ?1 AND la.startTime >= ?2 AND la.endTime <= ?3 AND la.isAvailable = true")
    List<LessonAvailability> findAvailableLessonsByPlaceAndTimeRange(Long placeId, LocalDateTime startTime, LocalDateTime endTime);
    
    @Query("SELECT la FROM LessonAvailability la WHERE la.school.id = ?1 AND la.startTime >= ?2 AND la.endTime <= ?3 AND la.isAvailable = true")
    List<LessonAvailability> findAvailableLessonsBySchoolAndTimeRange(Long schoolId, LocalDateTime startTime, LocalDateTime endTime);
    
    @Query("SELECT la FROM LessonAvailability la WHERE la.teacher.id = ?1 AND la.startTime >= ?2 AND la.endTime <= ?3 AND la.isAvailable = true")
    List<LessonAvailability> findAvailableLessonsByTeacherAndTimeRange(Long teacherId, LocalDateTime startTime, LocalDateTime endTime);
} 