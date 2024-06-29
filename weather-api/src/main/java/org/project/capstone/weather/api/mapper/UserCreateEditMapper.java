package org.project.capstone.weather.api.mapper;

import org.mapstruct.*;
import org.project.capstone.weather.api.dto.LocationResponse;
import org.project.capstone.weather.api.dto.UserCreateEditRequest;
import org.project.capstone.weather.api.dto.UserResponse;
import org.project.capstone.weather.api.entity.UserEntity;
import org.project.capstone.weather.api.entity.UserLocationsEntity;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {PasswordEncodingMapper.class, LocationMapper.class})
public interface UserCreateEditMapper {

    @Mapping(source = "password", target = "password", qualifiedBy = EncodedMapping.class)
    UserEntity userCreateEditToUser(UserCreateEditRequest userCreateEditDto);

    @Mapping(target = "locations", source = "userLocations")
    UserResponse userToUserResponse(UserEntity user);

    @Mapping(target = "password", ignore = true)
    UserEntity update(UserCreateEditRequest userCreateEditDto, @MappingTarget UserEntity user);

    @Mapping(target = "locations", source = "userLocations", qualifiedByName = "userLocationsToLocationResponse")
    List<LocationResponse> userLocationsToLocationResponse(List<UserLocationsEntity> userLocations);

    @AfterMapping
    default void afterMapping(@MappingTarget UserEntity user, UserCreateEditRequest userCreateEditDto) {
        if (userCreateEditDto.password() != null) {
            user.setPassword(PasswordEncodingMapper.encode(userCreateEditDto.password()));
        }
    }
}
