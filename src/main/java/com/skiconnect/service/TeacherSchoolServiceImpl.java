package com.skiconnect.service;

import com.skiconnect.model.School;
import com.skiconnect.model.Teacher;
import com.skiconnect.model.User;
import com.skiconnect.repository.SchoolRepository;
import com.skiconnect.repository.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TeacherSchoolServiceImpl implements TeacherSchoolService {

    private final TeacherRepository teacherRepository;
    private final SchoolRepository schoolRepository;
    private final UserService userService;
    private final PlaceService placeService;

    public TeacherSchoolServiceImpl(TeacherRepository teacherRepository, 
                                   SchoolRepository schoolRepository,
                                   UserService userService,
                                   PlaceService placeService) {
        this.teacherRepository = teacherRepository;
        this.schoolRepository = schoolRepository;
        this.userService = userService;
        this.placeService = placeService;
    }

    @Override
    public Teacher assignTeacherToSchool(Long teacherId, Long schoolId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with id: " + teacherId));
        
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalArgumentException("School not found with id: " + schoolId));
        
        if (!school.getTeachers().contains(teacher)) {
            school.getTeachers().add(teacher);
            teacher.setSchool(school);
            teacherRepository.save(teacher);
            schoolRepository.save(school);
        }
        
        return teacher;
    }

    @Override
    public Teacher removeTeacherFromSchool(Long teacherId, Long schoolId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with id: " + teacherId));
        
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalArgumentException("School not found with id: " + schoolId));
        
        if (school.getTeachers().remove(teacher)) {
            teacher.setSchool(null);
            teacherRepository.save(teacher);
            schoolRepository.save(school);
        }
        
        return teacher;
    }

    @Override
    public List<Teacher> getSchoolTeachers(Long schoolId) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalArgumentException("School not found with id: " + schoolId));
        return school.getTeachers();
    }

    @Override
    public School getTeacherSchool(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with id: " + teacherId));
        return teacher.getSchool();
    }

    @Override
    public Teacher createTeacherWithSchool(String name, Long userId, Long schoolId, boolean isFreelancer) {
        if (teacherRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("User already has a teacher profile");
        }

        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        School school = null;
        if (schoolId != null) {
            school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new IllegalArgumentException("School not found with id: " + schoolId));
        }

        Teacher teacher = new Teacher();
        teacher.setName(name);
        teacher.setUser(user);
        teacher.setSchool(school);
        teacher.setFreelancer(isFreelancer);

        Teacher savedTeacher = teacherRepository.save(teacher);
        
        if (school != null) {
            school.getTeachers().add(savedTeacher);
            schoolRepository.save(school);
        }

        return savedTeacher;
    }

    @Override
    public Teacher createFreelancerWithSchool(String name, Long userId, Long placeId) {
        if (teacherRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("User already has a teacher profile");
        }

        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        // Create a school for the freelancer
        School freelancerSchool = new School();
        freelancerSchool.setName(name + " Freelancer School");
        freelancerSchool.setUser(user);
        
        if (placeId != null) {
            freelancerSchool.setPlace(placeService.findById(placeId)
                    .orElseThrow(() -> new IllegalArgumentException("Place not found with id: " + placeId)));
        }
        
        freelancerSchool = schoolRepository.save(freelancerSchool);

        // Create the teacher as a freelancer
        Teacher teacher = new Teacher();
        teacher.setName(name);
        teacher.setUser(user);
        teacher.setSchool(freelancerSchool);
        teacher.setFreelancer(true);

        Teacher savedTeacher = teacherRepository.save(teacher);
        
        // Add the teacher to the school
        freelancerSchool.getTeachers().add(savedTeacher);
        schoolRepository.save(freelancerSchool);

        return savedTeacher;
    }
} 