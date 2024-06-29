package org.project.capstone.weather.api.util.converter;

import jakarta.persistence.AttributeConverter;
import org.project.capstone.weather.api.entity.WindDirection;

import java.util.Arrays;
import java.util.Optional;

public class WindDirectionConverter implements AttributeConverter<WindDirection, String> {

    @Override
    public String convertToDatabaseColumn(WindDirection attribute) {
        return Optional.of(attribute)
                .map(WindDirection::getAbbreviation)
                .orElseThrow();
    }

    @Override
    public WindDirection convertToEntityAttribute(String dbData) {
        return Arrays.stream(WindDirection.values())
                .filter(d -> d.getAbbreviation().equals(dbData))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown abbreviation " + dbData));
    }
}
