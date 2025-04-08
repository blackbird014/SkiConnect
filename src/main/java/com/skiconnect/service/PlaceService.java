package com.skiconnect.service;

import com.skiconnect.model.Place;

import java.util.Optional;

public interface PlaceService extends BaseService<Place, Long> {
    Optional<Place> findByName(String name);
    boolean existsByName(String name);
    Place createPlace(String name, String location);
} 