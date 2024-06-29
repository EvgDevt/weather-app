package org.project.capstone.weather.api.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.capstone.weather.api.dto.LocationRequest;
import org.project.capstone.weather.api.dto.LocationResponse;
import org.project.capstone.weather.api.entity.LocationEntity;
import org.project.capstone.weather.api.excpetion.LocationNotFoundException;
import org.project.capstone.weather.api.mapper.LocationMapper;
import org.project.capstone.weather.api.repository.LocationRepository;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {

    @Mock
    private LocationMapper locationMapper;

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationService locationService;

    private static LocationRequest locationRequest;

    @BeforeAll
    public static void setup() {
        locationRequest = new LocationRequest("Italy", "Rome");
    }

    @Test
    public void testGetLocationByCityAndCountry_whenSuchLocationExists_shouldReturnLocationResponse() {
        LocationEntity locationEntity = LocationEntity.builder()
                .id(1)
                .city("Rome")
                .country("Italy")
                .build();
        LocationResponse expectedResponse = new LocationResponse(1, "Italy", "Rome");

        when(locationRepository.findLocationEntitiesByCityAndCountry("Rome", "Italy")).thenReturn(Optional.of(locationEntity));
        when(locationMapper.locationToLocationResponse(locationEntity)).thenReturn(expectedResponse);

        LocationResponse actualResponse = locationService.getLocationByCityAndCountry(locationRequest);

        Assertions.assertThat(actualResponse).isEqualTo(expectedResponse);

        verify(locationMapper, times(1)).locationToLocationResponse(any(LocationEntity.class));
        verify(locationRepository, times(1)).findLocationEntitiesByCityAndCountry(anyString(), anyString());
        verifyNoMoreInteractions(locationMapper);
        verifyNoMoreInteractions(locationRepository);
    }

    @Test
    public void testGetLocationByCityAndCountry_whenSuchLocationDoesNotExist_shouldThrowLocationNotFoundException() {

        when(locationRepository.findLocationEntitiesByCityAndCountry("Rome", "Italy")).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> locationService.getLocationByCityAndCountry(locationRequest))
                .isInstanceOf(LocationNotFoundException.class);

        verify(locationRepository, times(1)).findLocationEntitiesByCityAndCountry(anyString(), anyString());
        verifyNoInteractions(locationMapper);
        verifyNoMoreInteractions(locationRepository);
    }
}
