package org.project.capstone.weather.api.integration.service;

import org.junit.jupiter.api.Test;
import org.project.capstone.weather.api.dto.AverageWeatherResponse;
import org.project.capstone.weather.api.dto.LocationResponse;
import org.project.capstone.weather.api.dto.WeatherResponse;
import org.project.capstone.weather.api.entity.WeatherCondition;
import org.project.capstone.weather.api.entity.WindDirection;
import org.project.capstone.weather.api.excpetion.LocationNotFoundException;
import org.project.capstone.weather.api.integration.IntegrationTestBase;
import org.project.capstone.weather.api.interceptor.UnitsContext;
import org.project.capstone.weather.api.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

public class WeatherServiceIT extends IntegrationTestBase {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private UnitsContext unitsContext;

    @Test
    public void testGetLatestWeatherByCity_whenCityExists_shouldReturnOptionalWeatherResponse_withDefaultUnits() {
        WeatherResponse expectedResponse = WeatherResponse.builder()
                .temperature(22.5)
                .feelsLikeTemperature(27.5)
                .windSpeed(5.5)
                .windDirection(WindDirection.NORTH_WEST)
                .weatherCondition(WeatherCondition.SUNNY)
                .locationDto(new LocationResponse(1, "USA", "New York"))
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();

        Optional<WeatherResponse> actualResponse = weatherService.getLatestWeatherByCity("New York");

        long secondsToTolerance = 1L;
        actualResponse.ifPresent(actual ->
                assertWeatherResponseWithTimestampTolerance(actual, expectedResponse, secondsToTolerance));
    }

    @Test
    public void testGetLatestWeatherByCity_whenImperialUnitsRequested_andCityExists_shouldReturnOptionalWeatherResponse() {
        unitsContext.setUnits("imperial");

        Optional<WeatherResponse> actualResponse = weatherService.getLatestWeatherByCity("New York");

        actualResponse.ifPresent(actual -> assertThat(actual.temperature()).isEqualTo(72.5));
    }

    @Test
    public void testGetLatestWeatherByCity_whenCityDoesNotExist_shouldReturnEmptyOptional() {

        Optional<WeatherResponse> actualResponse = weatherService.getLatestWeatherByCity("Shire");

        assertThat(actualResponse).isNotPresent();
    }

    @Test
    public void testGetSevenDaysAverageTemperatureByCity_whenCityExists_shouldReturnAverageTemperature_withDefaultUnits() {
        AverageWeatherResponse expectedResponse = new AverageWeatherResponse("New York", 22.0);

        AverageWeatherResponse actualResponse = weatherService.getSevenDaysAverageTemperatureByCity("New York");

        assertThat(actualResponse.averageTemperature()).isEqualTo(expectedResponse.averageTemperature());
    }

    @Test
    public void testGetSevenDaysAverageTemperatureByCity_whenCityDoesNotExist_shouldThrowLocationNotFoundException() {
        assertThatThrownBy(() -> weatherService.getSevenDaysAverageTemperatureByCity("Shire"))
                .isInstanceOf(LocationNotFoundException.class);
    }

    @Test
    public void testGetFullWeatherHistoryByCity_whenCityExists_shouldReturnWeatherResponseList_withDefaultPagination() {
        WeatherResponse expectedResponse = WeatherResponse.builder()
                .temperature(22.5)
                .feelsLikeTemperature(27.5)
                .windSpeed(5.5)
                .windDirection(WindDirection.NORTH_WEST)
                .weatherCondition(WeatherCondition.SUNNY)
                .locationDto(new LocationResponse(1, "USA", "New York"))
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .build();

        Pageable pageable = PageRequest.of(0, 20);

        List<WeatherResponse> actualResponseDateTruncated = weatherService.getFullWeatherHistoryByCity("New York", pageable)
                .stream()
                .map(actual -> actual.withCreatedAt(actual.createdAt().truncatedTo(ChronoUnit.MINUTES)))
                .toList();

        assertThat(actualResponseDateTruncated).isNotEmpty();
        assertThat(actualResponseDateTruncated).hasSize(8);

        assertThat(actualResponseDateTruncated).contains(expectedResponse);

    }

    @Test
    public void testGetFullWeatherHistoryByCity_whenCityExists_shouldReturnWeatherResponseList_withPagination() {
        WeatherResponse expectedResponse = WeatherResponse.builder()
                .temperature(22.5)
                .feelsLikeTemperature(27.5)
                .windSpeed(5.5)
                .windDirection(WindDirection.NORTH_WEST)
                .weatherCondition(WeatherCondition.SUNNY)
                .locationDto(new LocationResponse(1, "USA", "New York"))
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .build();

        WeatherResponse expectedResponse2 = WeatherResponse.builder()
                .temperature(21.5)
                .feelsLikeTemperature(26.5)
                .windSpeed(5.5)
                .windDirection(WindDirection.NORTH_WEST)
                .weatherCondition(WeatherCondition.SUNNY)
                .locationDto(new LocationResponse(1, "USA", "New York"))
                .createdAt(LocalDateTime.now().minusDays(1L).truncatedTo(ChronoUnit.MINUTES))
                .build();

        List<WeatherResponse> expectedResponses = List.of(expectedResponse, expectedResponse2);

        Pageable pageable = PageRequest.of(0, 2);

        List<WeatherResponse> actualResponseDateTruncated = weatherService.getFullWeatherHistoryByCity("New York", pageable)
                .stream()
                .map(actual -> actual.withCreatedAt(actual.createdAt().truncatedTo(ChronoUnit.MINUTES)))
                .toList();

        assertThat(actualResponseDateTruncated).isNotEmpty();
        assertThat(actualResponseDateTruncated).hasSize(2);

        assertThat(actualResponseDateTruncated).containsAnyElementsOf(expectedResponses);
    }

    private static void assertWeatherResponseWithTimestampTolerance(WeatherResponse actualResponse,
                                                                    WeatherResponse expectedResponse,
                                                                    long secondsToTolerance) {
        assertThat(actualResponse)
                .returns(expectedResponse.temperature(), from(WeatherResponse::temperature))
                .returns(expectedResponse.feelsLikeTemperature(), from(WeatherResponse::feelsLikeTemperature))
                .returns(expectedResponse.windSpeed(), from(WeatherResponse::windSpeed))
                .returns(expectedResponse.windDirection(), from(WeatherResponse::windDirection))
                .returns(expectedResponse.weatherCondition(), from(WeatherResponse::weatherCondition))
                .returns(expectedResponse.locationDto(), from(WeatherResponse::locationDto))
                .returns(expectedResponse.locationDto(), from(WeatherResponse::locationDto));

        long diffInSeconds = Math.abs(ChronoUnit.MINUTES.between(actualResponse.createdAt(), expectedResponse.createdAt()));
        assertThat(diffInSeconds).isLessThan(secondsToTolerance);
    }
}
