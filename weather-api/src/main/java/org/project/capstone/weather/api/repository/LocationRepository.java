package org.project.capstone.weather.api.repository;

import org.project.capstone.weather.api.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<LocationEntity, Integer> {

    Optional<LocationEntity> findLocationEntitiesByCityAndCountry(String city, String country);

    Optional<LocationEntity> findLocationEntityByCity(String city);
}
