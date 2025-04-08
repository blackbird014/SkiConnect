package com.skiconnect.service;

import com.skiconnect.model.School;
import com.skiconnect.model.Teacher;

import java.util.List;

public interface TeacherSchoolService {
    Teacher assignTeacherToSchool(Long teacherId, Long schoolId);
    Teacher removeTeacherFromSchool(Long teacherId, Long schoolId);
    List<Teacher> getSchoolTeachers(Long schoolId);
    School getTeacherSchool(Long teacherId);
    Teacher createTeacherWithSchool(String name, Long userId, Long schoolId, boolean isFreelancer);
    Teacher createFreelancerWithSchool(String name, Long userId, Long placeId);
} 