package org.project.capstone.weather.api.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WindDirection {

    NORTH("N"),
    EAST("E"),
    SOUTH("S"),
    WEST("W"),
    NORTH_EAST("NE"),
    SOUTH_EAST("SE"),
    SOUTH_WEST("SW"),
    NORTH_WEST("NW"),
    CALM("CLM");

    private final String abbreviation;
}
