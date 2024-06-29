package org.project.capstone.weather.api.controller;

import lombok.RequiredArgsConstructor;
import org.project.capstone.weather.api.controller.annotation.TemperatureConvertable;
import org.project.capstone.weather.api.dto.AverageWeatherResponse;
import org.project.capstone.weather.api.dto.WeatherResponse;
import org.project.capstone.weather.api.service.WeatherService;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@TemperatureConvertable
@RestController
@RequestMapping("/weather-api/v1/weather-data")
public class WeatherController {

    private final WeatherService weatherService;


    @GetMapping("/now")
    public WeatherResponse getCurrentWeatherByCity(@RequestParam(value = "city", required = false) String cityName) {
        return weatherService.getLatestWeatherByCity(cityName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("{city}/history")
    public List<WeatherResponse> getWeatherHistoryByCityAndDateRange(
            @PathVariable("city") String cityName,
            @RequestParam(value = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return weatherService.getWeatherHistoryByCityAndDateRange(cityName, startDate, endDate);
    }

    @GetMapping("{city}/history/all")
    public List<WeatherResponse> getFullWeatherHistoryByCity(@PathVariable("city") String cityName, Pageable pageable) {
        return weatherService.getFullWeatherHistoryByCity(cityName, pageable);
    }

    @GetMapping("{city}/7-days-average")
    public AverageWeatherResponse getSevenDaysAverageTemperatureByCity(@PathVariable("city") String city) {
        return weatherService.getSevenDaysAverageTemperatureByCity(city);
    }

    @GetMapping("/now/cities")
    public List<WeatherResponse> getWeatherByCities(@RequestParam("city") List<String> city) {
        return weatherService.getWeatherByCities(city);
    }
}
