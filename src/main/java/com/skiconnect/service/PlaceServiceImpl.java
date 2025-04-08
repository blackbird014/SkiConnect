package com.skiconnect.service;

import com.skiconnect.model.Place;
import com.skiconnect.repository.PlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class PlaceServiceImpl extends BaseServiceImpl<Place, Long> implements PlaceService {

    private final PlaceRepository placeRepository;

    public PlaceServiceImpl(PlaceRepository placeRepository) {
        super(placeRepository);
        this.placeRepository = placeRepository;
    }

    @Override
    public Optional<Place> findByName(String name) {
        return placeRepository.findByName(name);
    }

    @Override
    public boolean existsByName(String name) {
        return placeRepository.existsByName(name);
    }

    @Override
    public Place createPlace(String name, String location) {
        if (existsByName(name)) {
            throw new IllegalArgumentException("Place with name " + name + " already exists");
        }

        Place place = new Place();
        place.setName(name);
        place.setLocation(location);

        return save(place);
    }
} 