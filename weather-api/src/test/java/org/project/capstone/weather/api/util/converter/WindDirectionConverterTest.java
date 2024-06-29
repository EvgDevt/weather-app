package org.project.capstone.weather.api.util.converter;

import org.junit.jupiter.api.Test;
import org.project.capstone.weather.api.entity.WindDirection;
import org.project.capstone.weather.api.util.converter.WindDirectionConverter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WindDirectionConverterTest {

    private final WindDirectionConverter converter = new WindDirectionConverter();

    @Test
    void testConvertToDatabaseColumn() {
        WindDirection windDirection = WindDirection.NORTH;

        String abbreviation = converter.convertToDatabaseColumn(windDirection);

        assertEquals("N", abbreviation);
    }

    @Test
    void testConvertToEntityAttribute() {
        String abbreviation = "E";

        WindDirection windDirection = converter.convertToEntityAttribute(abbreviation);

        assertEquals(WindDirection.EAST, windDirection);
    }

}