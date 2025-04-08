package com.skiconnect.service;

import com.skiconnect.model.School;
import com.skiconnect.model.Teacher;
import com.skiconnect.model.User;
import com.skiconnect.repository.SchoolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SchoolServiceImpl extends BaseServiceImpl<School, Long> implements SchoolService {

    private final SchoolRepository schoolRepository;
    private final UserService userService;
    private final PlaceService placeService;

    public SchoolServiceImpl(SchoolRepository schoolRepository, UserService userService, 
                            PlaceService placeService) {
        super(schoolRepository);
        this.schoolRepository = schoolRepository;
        this.userService = userService;
        this.placeService = placeService;
    }

    @Override
    public Optional<School> findByName(String name) {
        return schoolRepository.findByName(name);
    }

    @Override
    public List<School> findByPlaceId(Long placeId) {
        return schoolRepository.findByPlaceId(placeId);
    }

    @Override
    public Optional<School> findByUserId(Long userId) {
        return schoolRepository.findByUserId(userId);
    }

    @Override
    public boolean existsByName(String name) {
        return schoolRepository.existsByName(name);
    }

    @Override
    public School createSchool(String name, Long userId, Long placeId) {
        if (existsByName(name)) {
            throw new IllegalArgumentException("School with name " + name + " already exists");
        }

        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        School school = new School();
        school.setName(name);
        school.setUser(user);
        
        if (placeId != null) {
            school.setPlace(placeService.findById(placeId)
                    .orElseThrow(() -> new IllegalArgumentException("Place not found with id: " + placeId)));
        }

        return save(school);
    }
} 