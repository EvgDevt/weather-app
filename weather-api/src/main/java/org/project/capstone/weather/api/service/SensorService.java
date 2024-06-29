package org.project.capstone.weather.api.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.project.capstone.weather.api.dto.LocationRequest;
import org.project.capstone.weather.api.dto.LocationResponse;
import org.project.capstone.weather.api.dto.SensorCreationRequest;
import org.project.capstone.weather.api.dto.SensorResponse;
import org.project.capstone.weather.api.dto.filter.LocationFilter;
import org.project.capstone.weather.api.dto.filter.QPredicate;
import org.project.capstone.weather.api.entity.LocationEntity;
import org.project.capstone.weather.api.entity.SensorEntity;
import org.project.capstone.weather.api.mapper.SensorMapper;
import org.project.capstone.weather.api.repository.SensorRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.project.capstone.weather.api.entity.QSensorEntity.sensorEntity;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class SensorService {

    private final SensorRepository sensorRepository;

    private final SensorMapper sensorMapper;

    private final LocationService locationService;


    public List<SensorResponse> getAllSensors(LocationFilter filter, Pageable pageable) {
        Predicate predicate = QPredicate.builder()
                .add(filter.country(), sensorEntity.location.country::containsIgnoreCase)
                .add(filter.city(), sensorEntity.location.city::containsIgnoreCase)
                .build();

        return sensorRepository.findAll(predicate, pageable)
                .stream()
                .map(sensorMapper::sensorEntityToSensorResponse)
                .toList();
    }

    public Optional<SensorResponse> getSensorById(Integer id) {
        return sensorRepository.findById(id)
                .map(sensorMapper::sensorEntityToSensorResponse);
    }

    @Transactional
    public boolean deleteSensor(Integer id) {
        return sensorRepository.findById(id).map(sensor -> {
            sensorRepository.delete(sensor);
            return true;
        }).orElse(false);
    }

    @Transactional
    public void addSensor(SensorCreationRequest request, LocationRequest locationRequest) {
        LocationResponse location = locationService.getLocationByCityAndCountry(locationRequest);

        SensorEntity sensor = sensorMapper.sensorRequestToSensorEntity(request);

        sensor.setLocation(LocationEntity.builder()
                .id(location.id())
                .country(location.country())
                .city(location.city())
                .build());

        sensorRepository.saveAndFlush(sensor);
    }
}
