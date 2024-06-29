package org.project.capstone.weather.api.service;

import lombok.RequiredArgsConstructor;
import org.project.capstone.weather.api.dto.LocationRequest;
import org.project.capstone.weather.api.dto.LocationResponse;
import org.project.capstone.weather.api.entity.UserLocationsEntity;
import org.project.capstone.weather.api.excpetion.LocationNotFoundException;
import org.project.capstone.weather.api.mapper.LocationMapper;
import org.project.capstone.weather.api.repository.LocationRepository;
import org.project.capstone.weather.api.repository.UserLocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    private final UserLocationRepository userLocationRepository;

    private final LocationMapper locationMapper;


    public LocationResponse getLocationByCityAndCountry(LocationRequest request) {
        String city = request.city();
        String country = request.country();

        return locationRepository.findLocationEntitiesByCityAndCountry(city, country)
                .map(locationMapper::locationToLocationResponse)
                .orElseThrow(LocationNotFoundException::new);
    }

    public LocationResponse getLocationByCity(String city) {
        return locationRepository.findLocationEntityByCity(city)
                .map(locationMapper::locationToLocationResponse)
                .orElseThrow(LocationNotFoundException::new);
    }

    public List<LocationResponse> getUserLocationsByUserId(Integer id) {
        List<UserLocationsEntity> userLocations = userLocationRepository.findAllByUserId(id);

        return userLocations.isEmpty() ? Collections.emptyList() : userLocations.stream()
                .map(UserLocationsEntity::getLocation)
                .map(locationMapper::locationToLocationResponse)
                .collect(Collectors.toList());
    }
}
