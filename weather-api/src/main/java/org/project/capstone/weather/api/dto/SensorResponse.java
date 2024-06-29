package org.project.capstone.weather.api.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SensorResponse(
        String model,
        LocationResponse location,
        LocalDateTime createdAt,
        String createdBy
) {
}
