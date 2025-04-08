package com.skiconnect.service;

import com.skiconnect.model.Teacher;

import java.util.List;
import java.util.Optional;

public interface TeacherService extends BaseService<Teacher, Long> {
    List<Teacher> findBySchoolId(Long schoolId);
    Optional<Teacher> findByUserId(Long userId);
    List<Teacher> findByIsFreelancer(boolean isFreelancer);
    List<Teacher> findByPlaceId(Long placeId);
    boolean existsByUserId(Long userId);
    Teacher createTeacher(String name, Long userId, Long schoolId, boolean isFreelancer);
    Teacher createFreelancer(String name, Long userId, Long placeId);
} 