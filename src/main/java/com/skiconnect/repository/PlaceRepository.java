package com.skiconnect.repository;

import com.skiconnect.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    Optional<Place> findByName(String name);
    boolean existsByName(String name);
} 