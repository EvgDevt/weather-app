package org.project.capstone.weather.api.util.converter;

import org.junit.jupiter.api.Test;
import org.project.capstone.weather.api.dto.LocationResponse;
import org.project.capstone.weather.api.dto.WeatherResponse;
import org.project.capstone.weather.api.entity.WeatherCondition;
import org.project.capstone.weather.api.entity.WindDirection;
import org.project.capstone.weather.api.util.converter.TemperatureConverter;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

class TemperatureConverterTest {

    @Test
    public void testConvertTemperature_whenUnitsAreImperial_shouldConvertToImperial() {
        WeatherResponse response = buildWeatherResponse();

        WeatherResponse convertedResponse = TemperatureConverter.convertTemperature(response, "imperial");

        assertThat(convertedResponse.temperature()).isEqualTo(TemperatureConverter.celsiusToFahrenheit(20.0));
        assertThat(convertedResponse.feelsLikeTemperature()).isEqualTo(TemperatureConverter.celsiusToFahrenheit(22.0));
    }

    @Test
    public void testConvertTemperature_whenUnitsAreMetric_shouldNotConvert() {
        WeatherResponse response = buildWeatherResponse();

        WeatherResponse convertedResponse = TemperatureConverter.convertTemperature(response, "metric");

        assertThat(convertedResponse.temperature()).isEqualTo(20.0);
        assertThat(convertedResponse.feelsLikeTemperature()).isEqualTo(22.0);
    }

    @Test
    public void testCelsiusToFahrenheit() {
        double celsius = 20.0;
        double expectedFahrenheit = (celsius * 9 / 5) + 32;

        double actualFahrenheit = TemperatureConverter.celsiusToFahrenheit(celsius);

        assertThat(actualFahrenheit).isEqualTo(expectedFahrenheit);
    }

    private WeatherResponse buildWeatherResponse() {
        return WeatherResponse.builder()
                .temperature(20.0)
                .feelsLikeTemperature(22.0)
                .windSpeed(1.0)
                .windDirection(WindDirection.NORTH)
                .weatherCondition(WeatherCondition.SUNNY)
                .locationDto(new LocationResponse(1, "London", "United Kingdom"))
                .createdAt(LocalDateTime.of(2024, Month.MAY, 22, 10, 10, 10))
                .build();
    }
}