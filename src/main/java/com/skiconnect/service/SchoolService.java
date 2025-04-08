package com.skiconnect.service;

import com.skiconnect.model.School;
import com.skiconnect.model.Teacher;

import java.util.List;
import java.util.Optional;

public interface SchoolService extends BaseService<School, Long> {
    Optional<School> findByName(String name);
    List<School> findByPlaceId(Long placeId);
    Optional<School> findByUserId(Long userId);
    boolean existsByName(String name);
    School createSchool(String name, Long userId, Long placeId);
} 