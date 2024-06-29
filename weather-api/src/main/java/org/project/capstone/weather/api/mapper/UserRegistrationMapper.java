package org.project.capstone.weather.api.mapper;

import org.mapstruct.*;
import org.project.capstone.weather.api.dto.auth.RegistrationRequest;
import org.project.capstone.weather.api.entity.Role;
import org.project.capstone.weather.api.entity.UserEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = PasswordEncodingMapper.class)
public interface UserRegistrationMapper {

    @Mapping(source = "password", target = "password", qualifiedBy = EncodedMapping.class)
    UserEntity registrationRequestToUser(RegistrationRequest request);

    @AfterMapping
    default void setDefaultRoleAfterUserRegistration(@MappingTarget UserEntity.UserEntityBuilder user, RegistrationRequest request) {
        if (request.password() != null) {
            PasswordEncodingMapper.encode(request.password());
        }
        user.role(Role.USER);
    }
}
