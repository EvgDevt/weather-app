package org.project.capstone.weather.api.repository;

import org.project.capstone.weather.api.entity.UserLocationsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserLocationRepository extends JpaRepository<UserLocationsEntity, Integer> {

    List<UserLocationsEntity> findAllByUserId(Integer id);
}
