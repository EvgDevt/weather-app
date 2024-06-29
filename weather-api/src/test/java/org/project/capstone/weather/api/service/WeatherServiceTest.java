package org.project.capstone.weather.api.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.capstone.weather.api.dto.AverageWeatherResponse;
import org.project.capstone.weather.api.dto.LocationResponse;
import org.project.capstone.weather.api.dto.WeatherResponse;
import org.project.capstone.weather.api.entity.*;
import org.project.capstone.weather.api.interceptor.UnitsContext;
import org.project.capstone.weather.api.mapper.WeatherMapper;
import org.project.capstone.weather.api.repository.WeatherRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {

    private static final String CITY = "London";

    @Mock
    private WeatherRepository weatherRepository;

    @Mock
    private WeatherMapper weatherMapper;

    @Mock
    private UnitsContext unitsContext;

    @InjectMocks
    private WeatherService weatherService;

    @BeforeEach
    public void setup() {
        lenient().when(unitsContext.getUnits()).thenReturn("metric");
    }

    @Test
    public void testGetLatestWeatherByCity_whenCityExists_shouldReturnWeatherResponse() {
        WeatherResponse expectedResponse = buildWeatherResponse(20.0, LocalDateTime.of(2024, Month.MAY, 22, 10, 10, 10));
        WeatherEntity weather = buildWeatherEntity(20.0, LocalDateTime.of(2024, Month.MAY, 22, 10, 10, 10));

        when(weatherRepository.findLatestWeatherByCity(anyString())).thenReturn(Optional.of(weather));
        when(weatherMapper.weatherToWeatherResponse(weather)).thenReturn(expectedResponse);

        Optional<WeatherResponse> actualResponse = weatherService.getLatestWeatherByCity(CITY);

        Assertions.assertThat(actualResponse).contains(expectedResponse);

        verify(weatherRepository, times(1)).findLatestWeatherByCity(anyString());
    }

    @Test
    public void testGetWeatherHistoryByCityAndDateRange_whenValidDateRange_shouldReturnWeatherResponseList() {
        LocalDate startDate = LocalDate.of(2024, Month.MAY, 1);
        LocalDate endDate = LocalDate.of(2024, Month.MAY, 10);
        WeatherEntity weather = buildWeatherEntity(20.0, LocalDateTime.of(2024, Month.MAY, 5, 10, 10, 10));
        WeatherResponse expectedResponse = buildWeatherResponse(20.0, LocalDateTime.of(2024, Month.MAY, 5, 10, 10, 10));

        when(weatherRepository.findByCityAndDateRange(eq(CITY), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(weather));
        when(weatherMapper.weatherToWeatherResponse(weather)).thenReturn(expectedResponse);

        List<WeatherResponse> actualResponse = weatherService.getWeatherHistoryByCityAndDateRange(CITY, startDate, endDate);

        Assertions.assertThat(actualResponse).containsExactly(expectedResponse);

        verify(weatherRepository, times(1)).findByCityAndDateRange(eq(CITY), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    public void testGetWeatherHistoryByCityAndDateRange_whenInvalidDateRange_shouldThrowIllegalArgumentException() {
        LocalDate startDate = LocalDate.of(2024, Month.MAY, 1);
        LocalDate endDate = LocalDate.of(2024, Month.APRIL, 10);

        Assertions.assertThatThrownBy(() -> weatherService.getWeatherHistoryByCityAndDateRange(CITY, startDate, endDate))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(weatherRepository);
    }

    @Test
    public void testGetWeekAverageTemperatureByCity_shouldReturnAverageTemperature() {
        WeatherEntity weather = buildWeatherEntity(20.0, LocalDateTime.now());
        WeatherEntity weather2 = buildWeatherEntity(22.0, LocalDateTime.now());

        when(weatherRepository.findByCityAndDateRange(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(weather, weather2));

        AverageWeatherResponse actualResponse = weatherService.getSevenDaysAverageTemperatureByCity(CITY);

        Assertions.assertThat(actualResponse.city()).isEqualTo(CITY);
        Assertions.assertThat(actualResponse.averageTemperature()).isEqualTo(21.0);

        verify(weatherRepository, times(1)).findByCityAndDateRange(anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    public void testGetWeekAverageTemperatureByCity_whenImperialUnitsRequested_shouldReturnAverageTemperatureInFahrenheit() {
        WeatherEntity weather = buildWeatherEntity(20.0, LocalDateTime.now());
        WeatherEntity weather2 = buildWeatherEntity(22.0, LocalDateTime.now());

        when(weatherRepository.findByCityAndDateRange(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(weather, weather2));
        when(unitsContext.getUnits()).thenReturn("imperial");

        AverageWeatherResponse actualResponse = weatherService.getSevenDaysAverageTemperatureByCity(CITY);

        double averageTemperatureCelsius = (20.0 + 22.0) / 2;
        double expectedTemperatureFahrenheit = averageTemperatureCelsius * 9 / 5 + 32;

        Assertions.assertThat(actualResponse.city()).isEqualTo(CITY);
        Assertions.assertThat(actualResponse.averageTemperature()).isEqualTo(expectedTemperatureFahrenheit);

        verify(weatherRepository, times(1)).findByCityAndDateRange(anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    public void testGetFullWeatherHistoryByCity_shouldReturnWeatherResponseList() {
        WeatherEntity weather = buildWeatherEntity(20.0, LocalDateTime.now());
        WeatherEntity weather2 = buildWeatherEntity(22.0, LocalDateTime.now().plusHours(1));

        WeatherResponse expectedResponse = buildWeatherResponse(20.0, LocalDateTime.of(2024, Month.MAY, 22, 10, 10, 10));
        WeatherResponse expectedResponse2 = buildWeatherResponse(22.0, LocalDateTime.of(2024, Month.MAY, 22, 11, 10, 10));

        List<WeatherResponse> expectedResponses = List.of(expectedResponse, expectedResponse2);
        Pageable pageable = PageRequest.of(0, 10);

        when(weatherRepository.findAllByLocationCityIgnoreCaseOrderByMeasurementCreatedAtDesc(anyString(), eq(pageable)))
                .thenReturn(List.of(weather, weather2));
        when(weatherMapper.weatherToWeatherResponse(weather)).thenReturn(expectedResponse);
        when(weatherMapper.weatherToWeatherResponse(weather2)).thenReturn(expectedResponse2);

        List<WeatherResponse> actualResponse = weatherService.getFullWeatherHistoryByCity(CITY, pageable);

        Assertions.assertThat(actualResponse).isEqualTo(expectedResponses);

        verify(weatherRepository, times(1)).findAllByLocationCityIgnoreCaseOrderByMeasurementCreatedAtDesc(anyString(), eq(pageable));
    }

    private WeatherEntity buildWeatherEntity(Double temperature, LocalDateTime createdAt) {
        return WeatherEntity.builder()
                .id(1)
                .measurement(MeasurementEmbedded.builder()
                        .temperature(temperature)
                        .windSpeed(1.0)
                        .windDirection(WindDirection.NORTH)
                        .createdAt(createdAt)
                        .sensor(SensorEntity.builder()
                                .id(1)
                                .model("WSMP-500")
                                .location(LocationEntity.builder()
                                        .id(1)
                                        .city("London")
                                        .country("United Kingdom")
                                        .build())
                                .build())
                        .build())
                .location(LocationEntity.builder()
                        .id(1)
                        .city("London")
                        .country("United Kingdom")
                        .build())
                .build();
    }

    private WeatherResponse buildWeatherResponse(Double temperature, LocalDateTime createdAt) {
        return WeatherResponse.builder()
                .temperature(temperature)
                .feelsLikeTemperature(temperature + 5)
                .windSpeed(1.0)
                .windDirection(WindDirection.NORTH)
                .weatherCondition(WeatherCondition.SUNNY)
                .locationDto(new LocationResponse(1, "London", "United Kingdom"))
                .createdAt(createdAt)
                .build();
    }
}
