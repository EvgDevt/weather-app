package org.project.capstone.weather.api.service;

import lombok.RequiredArgsConstructor;
import org.project.capstone.weather.api.dto.AverageWeatherResponse;
import org.project.capstone.weather.api.dto.WeatherResponse;
import org.project.capstone.weather.api.excpetion.LocationNotFoundException;
import org.project.capstone.weather.api.interceptor.UnitsContext;
import org.project.capstone.weather.api.mapper.WeatherMapper;
import org.project.capstone.weather.api.repository.WeatherRepository;
import org.project.capstone.weather.api.util.converter.TemperatureConverter;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherRepository weatherRepository;

    private final WeatherMapper weatherMapper;

    private final UnitsContext unitsContext;


    public Optional<WeatherResponse> getLatestWeatherByCity(String city) {
        return weatherRepository.findLatestWeatherByCity(city)
                .map(weather -> {
                    WeatherResponse response = weatherMapper.weatherToWeatherResponse(weather);
                    return TemperatureConverter.convertTemperature(response, unitsContext.getUnits());
                });
    }

    public List<WeatherResponse> getWeatherHistoryByCityAndDateRange(String cityName,
                                                                     LocalDate startDate,
                                                                     LocalDate endDate) {

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1L).atStartOfDay();

        return weatherRepository.findByCityAndDateRange(cityName, startDateTime, endDateTime)
                .stream()
                .map(weather -> {
                    WeatherResponse response = weatherMapper.weatherToWeatherResponse(weather);
                    return TemperatureConverter.convertTemperature(response, unitsContext.getUnits());
                })
                .toList();
    }

    @Cacheable("sevenDaysAverage")
    public AverageWeatherResponse getSevenDaysAverageTemperatureByCity(String city) {
        LocalDateTime endDateTime = LocalDateTime.now();
        LocalDateTime startDateTime = endDateTime.minusDays(7L);

        double average = weatherRepository.findByCityAndDateRange(city, startDateTime, endDateTime).stream()
                .mapToDouble(w -> w.getMeasurement().getTemperature())
                .average()
                .orElseThrow(LocationNotFoundException::new);

        if ("imperial".equalsIgnoreCase(unitsContext.getUnits())) {
            average = TemperatureConverter.celsiusToFahrenheit(average);
        }

        return new AverageWeatherResponse(city, average);
    }

    public List<WeatherResponse> getFullWeatherHistoryByCity(String cityName, Pageable pageable) {
        return weatherRepository.findAllByLocationCityIgnoreCaseOrderByMeasurementCreatedAtDesc(cityName, pageable)
                .stream()
                .map(weather -> {
                    WeatherResponse response = weatherMapper.weatherToWeatherResponse(weather);
                    return TemperatureConverter.convertTemperature(response, unitsContext.getUnits());
                })
                .toList();
    }

    public List<WeatherResponse> getWeatherByCities(List<String> cities) {
        return weatherRepository.findWeatherByCities(cities).stream()
                .map(weather -> {
                    WeatherResponse response = weatherMapper.weatherToWeatherResponse(weather);
                    return TemperatureConverter.convertTemperature(response, unitsContext.getUnits());
                })
                .toList();
    }
}
