package org.project.capstone.weather.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.project.capstone.weather.api.dto.LocationResponse;
import org.project.capstone.weather.api.entity.LocationEntity;
import org.project.capstone.weather.api.entity.UserLocationsEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LocationMapper {

    LocationResponse locationToLocationResponse(LocationEntity location);

    @Mapping(target = "id", source = "location.id")
    @Mapping(target = "city", source = "location.city")
    @Mapping(target = "country", source = "location.country")
    LocationResponse userLocationsToLocation(UserLocationsEntity userLocationsEntity);
}
