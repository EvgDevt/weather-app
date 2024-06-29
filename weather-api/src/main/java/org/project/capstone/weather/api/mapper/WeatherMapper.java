package org.project.capstone.weather.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.project.capstone.weather.api.dto.WeatherResponse;
import org.project.capstone.weather.api.entity.WeatherCondition;
import org.project.capstone.weather.api.entity.WeatherEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = LocationMapper.class)
public interface WeatherMapper {

    String FEELS_LIKE_TEMP_CALCULATION_EXPR =
            "java(calculateFeelsLikeTemperature(weather.getMeasurement().getWeatherCondition(), weather.getMeasurement().getTemperature()))";

    @Mapping(source = "measurement.temperature", target = "temperature")
    @Mapping(source = "measurement.windSpeed", target = "windSpeed")
    @Mapping(source = "measurement.windDirection", target = "windDirection")
    @Mapping(source = "measurement.humidity", target = "humidity")
    @Mapping(source = "measurement.weatherCondition", target = "weatherCondition")
    @Mapping(source = "measurement.createdAt", target = "createdAt")
    @Mapping(source = "location", target = "locationDto")
    @Mapping(target = "feelsLikeTemperature", expression = FEELS_LIKE_TEMP_CALCULATION_EXPR)
    WeatherResponse weatherToWeatherResponse(WeatherEntity weather);

    default Double calculateFeelsLikeTemperature(WeatherCondition weatherCondition, Double actualTemperature) {
        if (weatherCondition == null) {
            throw new IllegalStateException("Weather condition cannot be null");
        }
        Double feelsLikeTemp;
        switch (weatherCondition) {
            case ICY, HAIL, SNOWY, FREEZING_RAIN, TORNADO -> feelsLikeTemp = actualTemperature - 7.0;
            case RAINY, RAIN, THUNDERSTORM, WINDY, OVERCAST, FOGGY, MIST -> feelsLikeTemp = actualTemperature - 3.0;
            case SUNNY, CLOUDLESS -> feelsLikeTemp = actualTemperature + 5.0;
            case SANDSTORM, CLOUDY -> feelsLikeTemp = actualTemperature;
            default -> throw new IllegalStateException("Unknown Weather Condition " + weatherCondition);
        }
        return feelsLikeTemp;
    }
}
