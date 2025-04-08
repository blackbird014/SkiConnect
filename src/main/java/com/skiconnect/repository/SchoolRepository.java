package com.skiconnect.repository;

import com.skiconnect.model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
    Optional<School> findByName(String name);
    List<School> findByPlaceId(Long placeId);
    Optional<School> findByUserId(Long userId);
    boolean existsByName(String name);
} 