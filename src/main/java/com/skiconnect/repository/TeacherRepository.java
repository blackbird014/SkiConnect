package com.skiconnect.repository;

import com.skiconnect.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    List<Teacher> findBySchoolId(Long schoolId);
    Optional<Teacher> findByUserId(Long userId);
    List<Teacher> findByIsFreelancer(boolean isFreelancer);
    boolean existsByUserId(Long userId);
    List<Teacher> findBySchoolPlaceId(Long placeId);
} 