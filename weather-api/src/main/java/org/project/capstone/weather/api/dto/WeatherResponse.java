package org.project.capstone.weather.api.dto;

import lombok.Builder;
import lombok.With;
import org.project.capstone.weather.api.entity.WeatherCondition;
import org.project.capstone.weather.api.entity.WindDirection;

import java.time.LocalDateTime;

@Builder
@With
public record WeatherResponse(
        Double temperature,
        Double feelsLikeTemperature,
        Double windSpeed,
        WindDirection windDirection,
        Double humidity,
        WeatherCondition weatherCondition,
        LocationResponse locationDto,
        LocalDateTime createdAt) {
}
