package org.project.capstone.weather.api.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.capstone.weather.api.dto.AverageWeatherResponse;
import org.project.capstone.weather.api.dto.LocationResponse;
import org.project.capstone.weather.api.dto.WeatherResponse;
import org.project.capstone.weather.api.entity.WeatherCondition;
import org.project.capstone.weather.api.entity.WindDirection;
import org.project.capstone.weather.api.service.WeatherService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WeatherController.class)
@DisplayName("Weather Controller Unit tests")
@AutoConfigureMockMvc(addFilters = false)
public class WeatherControllerTest extends BaseControllerTest {

    private static final String CITY = "London";

    @MockBean
    private WeatherService weatherService;

    private static WeatherResponse response;

    @BeforeAll
    public static void setup() {
        response = WeatherResponse.builder()
                .temperature(20.0)
                .feelsLikeTemperature(25.0)
                .windSpeed(1.0)
                .windDirection(WindDirection.NORTH)
                .weatherCondition(WeatherCondition.SUNNY)
                .locationDto(new LocationResponse(1, "United Kingdom", "London"))
                .createdAt(LocalDateTime.of(2024, Month.MAY, 21, 12, 11, 10))
                .build();
    }

    @Test
    @DisplayName("Get Current Weather By City - Should Return WeatherResponse and Status 200 When City Exists")
    public void testGetCurrentWeatherByCity_whenCityExists_shouldReturnWeatherResponse_andStatusOk() throws Exception {
        when(weatherService.getLatestWeatherByCity(CITY)).thenReturn(Optional.of(response));

        mockMvc.perform(get("/weather-api/v1/weather-data/now")
                        .param("city", CITY))
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                                {
                                    "temperature": 20.0,
                                    "feelsLikeTemperature": 25.0,
                                    "windSpeed": 1.0,
                                    "windDirection": "NORTH",
                                    "weatherCondition": "SUNNY",
                                    "locationDto": {
                                        "id": 1,
                                        "city": "London",
                                        "country": "United Kingdom"
                                    },
                                    "createdAt": "2024-05-21T12:11:10"
                                }
                                """)
                );

        verify(weatherService, times(1)).getLatestWeatherByCity(anyString());
    }

    @Test
    @DisplayName("Get Current Weather By City - Should Return 404 When City Does Not Exist")
    public void testGetCurrentWeatherByCity_whenCityDoesNotExists_shouldReturnNotFound() throws Exception {
        when(weatherService.getLatestWeatherByCity(CITY)).thenReturn(Optional.empty());

        mockMvc.perform(get("/weather-api/v1/weather-data/{city}/now", CITY))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        result -> Assertions.assertThat(result.getResolvedException())
                                .isInstanceOf(NoResourceFoundException.class)
                                .hasMessageStartingWith("No static resource")
                );

        verifyNoInteractions(weatherService);
    }

    @Test
    @DisplayName("Get Weather History By City And Date Range - Should Return Weather Data of Specified Date Range When City Exists")
    public void testGetWeatherHistoryByCityAndDateRange_whenCityExists_shouldReturnWeatherDataOfSpecifiedDateRange() throws Exception {
        List<WeatherResponse> responses = List.of(
                WeatherResponse.builder().temperature(20.0).build(),
                WeatherResponse.builder().temperature(21.0).build(),
                WeatherResponse.builder().temperature(22.0).build()
        );
        LocalDate startDate = LocalDate.of(2024, Month.MAY, 21);
        LocalDate endDate = startDate.plusDays(2);

        when(weatherService.getWeatherHistoryByCityAndDateRange(anyString(), eq(startDate), eq(endDate)))
                .thenReturn(responses);

        mockMvc.perform(get("/weather-api/v1/weather-data/{city}/history", CITY)
                        .param("startDate", String.valueOf(startDate))
                        .param("endDate", String.valueOf(endDate)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(3))
                );
        verify(weatherService, times(1))
                .getWeatherHistoryByCityAndDateRange(anyString(), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("Get Full Weather History By City - Should Return Ok When Second Page Is Requested")
    public void testGetFullWeatherHistoryByCity_whenSecondPageIsRequested_shouldReturnOk() throws Exception {
        List<WeatherResponse> responses = List.of(
                WeatherResponse.builder().temperature(20.0).build(),
                WeatherResponse.builder().temperature(21.0).build(),
                WeatherResponse.builder().temperature(22.0).build()
        );

        when(weatherService.getFullWeatherHistoryByCity(eq(CITY), eq(PageRequest.of(1, 3)))).thenReturn(responses);

        mockMvc.perform(get("/weather-api/v1/weather-data/{city}/history/all", CITY)
                        .param("page", "1")
                        .param("size", "3")
                )
                .andExpectAll(
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$[0].temperature", is(20.0)),
                        jsonPath("$[1].temperature", is(21.0)),
                        jsonPath("$[2].temperature", is(22.0))
                );

        verify(weatherService, times(1)).getFullWeatherHistoryByCity(anyString(), any(Pageable.class));
    }

    @Test
    @DisplayName("Get Average Temperature By City - Should Return AverageWeatherResponse and Status 200 When City Exists")
    public void testGetAverageTemperatureByCity_whenCityExists_shouldReturnAverageWeatherResponse_andStatusOk() throws Exception {
        AverageWeatherResponse weatherResponse = new AverageWeatherResponse("London", 22.0);

        when(weatherService.getSevenDaysAverageTemperatureByCity(eq(CITY))).thenReturn(weatherResponse);

        mockMvc.perform(get("/weather-api/v1/weather-data/{city}/7-days-average", CITY))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "city": "London",
                                    "averageTemperature": 22.0
                                }
                                """)
                );

        verify(weatherService, times(1)).getSevenDaysAverageTemperatureByCity(anyString());
    }
}
