package com.skiconnect.service;

import com.skiconnect.model.Teacher;
import com.skiconnect.repository.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TeacherServiceImpl extends BaseServiceImpl<Teacher, Long> implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherSchoolService teacherSchoolService;

    public TeacherServiceImpl(TeacherRepository teacherRepository, 
                             TeacherSchoolService teacherSchoolService) {
        super(teacherRepository);
        this.teacherRepository = teacherRepository;
        this.teacherSchoolService = teacherSchoolService;
    }

    @Override
    public List<Teacher> findBySchoolId(Long schoolId) {
        return teacherRepository.findBySchoolId(schoolId);
    }

    @Override
    public Optional<Teacher> findByUserId(Long userId) {
        return teacherRepository.findByUserId(userId);
    }

    @Override
    public List<Teacher> findByIsFreelancer(boolean isFreelancer) {
        return teacherRepository.findByIsFreelancer(isFreelancer);
    }

    @Override
    public List<Teacher> findByPlaceId(Long placeId) {
        return teacherRepository.findBySchoolPlaceId(placeId);
    }

    @Override
    public boolean existsByUserId(Long userId) {
        return teacherRepository.existsByUserId(userId);
    }

    @Override
    public Teacher createTeacher(String name, Long userId, Long schoolId, boolean isFreelancer) {
        return teacherSchoolService.createTeacherWithSchool(name, userId, schoolId, isFreelancer);
    }

    @Override
    public Teacher createFreelancer(String name, Long userId, Long placeId) {
        return teacherSchoolService.createFreelancerWithSchool(name, userId, placeId);
    }
} 