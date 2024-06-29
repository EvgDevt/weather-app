package org.project.capstone.weather.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.project.capstone.weather.api.dto.SensorCreationRequest;
import org.project.capstone.weather.api.dto.SensorResponse;
import org.project.capstone.weather.api.entity.SensorEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = LocationMapper.class)
public interface SensorMapper {

    SensorResponse sensorEntityToSensorResponse(SensorEntity sensor);

    SensorEntity sensorRequestToSensorEntity(SensorCreationRequest request);
}
