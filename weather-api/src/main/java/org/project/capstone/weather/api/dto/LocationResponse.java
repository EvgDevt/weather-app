package org.project.capstone.weather.api.dto;

import lombok.Builder;

@Builder
public record LocationResponse(Integer id, String country, String city) {
}
