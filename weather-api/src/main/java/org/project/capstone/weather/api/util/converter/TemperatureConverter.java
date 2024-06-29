package org.project.capstone.weather.api.util.converter;

import lombok.experimental.UtilityClass;
import org.project.capstone.weather.api.dto.WeatherResponse;

@UtilityClass
public class TemperatureConverter {

    public static WeatherResponse convertTemperature(WeatherResponse response, String units) {
        if (units.equalsIgnoreCase("imperial")) {
            return response.withTemperature(TemperatureConverter.celsiusToFahrenheit(response.temperature()))
                    .withFeelsLikeTemperature(TemperatureConverter.celsiusToFahrenheit(response.feelsLikeTemperature()));
        }
        return response;
    }

    public static double celsiusToFahrenheit(double celsius) {
        return celsius * 9 / 5 + 32;
    }
}
