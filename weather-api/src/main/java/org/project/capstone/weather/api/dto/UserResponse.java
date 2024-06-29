package org.project.capstone.weather.api.dto;

import org.project.capstone.weather.api.entity.Role;

import java.util.List;

public record UserResponse(
        Integer id,
        String email,
        String firstname,
        String lastname,
        Role role,
        List<LocationResponse> locations) {

}
