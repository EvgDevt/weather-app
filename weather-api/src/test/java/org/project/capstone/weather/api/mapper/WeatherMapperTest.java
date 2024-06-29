package org.project.capstone.weather.api.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.capstone.weather.api.dto.WeatherResponse;
import org.project.capstone.weather.api.entity.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class WeatherMapperTest {

    @InjectMocks
    private WeatherMapper weatherMapper = new WeatherMapperImpl();

    @Mock
    private LocationMapper locationMapper;

    @Test
    void testWeatherToWeatherResponse_whenWeatherDataRequested_shouldProperlyMapToWeatherResponse() {
        WeatherEntity weather = WeatherEntity.builder()
                .id(1)
                .measurement(MeasurementEmbedded.builder()
                        .temperature(20.0)
                        .windSpeed(2.0)
                        .windDirection(WindDirection.CALM)
                        .weatherCondition(WeatherCondition.CLOUDLESS)
                        .build())
                .location(LocationEntity.builder()
                        .city("City")
                        .country("Country")
                        .build())
                .build();

        WeatherResponse weatherResponse = weatherMapper.weatherToWeatherResponse(weather);

        assertEquals(2.0, weatherResponse.windSpeed());
        assertEquals(WindDirection.CALM.getAbbreviation(), weatherResponse.windDirection().getAbbreviation());
        assertEquals(25.0, weatherResponse.feelsLikeTemperature());

    }

    @Test
    void testCalculateFeelsLikeTemperature_shouldProperlyCalculateAccordingToWeatherCondition() {
        assertEquals(13.0, weatherMapper.calculateFeelsLikeTemperature(WeatherCondition.ICY, 20.0));
        assertEquals(17.0, weatherMapper.calculateFeelsLikeTemperature(WeatherCondition.RAINY, 20.0));
        assertEquals(25.0, weatherMapper.calculateFeelsLikeTemperature(WeatherCondition.CLOUDLESS, 20.0));
        assertEquals(20.0, weatherMapper.calculateFeelsLikeTemperature(WeatherCondition.CLOUDY, 20.0));
    }

    @Test
    public void testCalculateFeelsLikeTemperature_whenNoWeatherConditionProvided_shouldThrowIllegalStateException() {
        Exception exception = assertThrows(IllegalStateException.class,
                () -> weatherMapper.calculateFeelsLikeTemperature(null, 20.0));
        assertEquals("Weather condition cannot be null", exception.getMessage());
    }
}