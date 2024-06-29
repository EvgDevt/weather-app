package org.project.capstone.weather.api.service;

import com.querydsl.core.types.Predicate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.capstone.weather.api.dto.LocationRequest;
import org.project.capstone.weather.api.dto.LocationResponse;
import org.project.capstone.weather.api.dto.SensorCreationRequest;
import org.project.capstone.weather.api.dto.SensorResponse;
import org.project.capstone.weather.api.dto.filter.LocationFilter;
import org.project.capstone.weather.api.entity.LocationEntity;
import org.project.capstone.weather.api.entity.SensorEntity;
import org.project.capstone.weather.api.excpetion.LocationNotFoundException;
import org.project.capstone.weather.api.mapper.SensorMapper;
import org.project.capstone.weather.api.repository.SensorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SensorServiceTest {

    private static final Integer SENSOR_ID = 1;

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private SensorMapper sensorMapper;

    @Mock
    private LocationService locationService;

    @InjectMocks
    private SensorService sensorService;

    @Test
    public void testGetAllSensors_whenFilterIsNotSet_shouldReturnSensorList() {
        SensorEntity sensor = SensorEntity.builder()
                .id(1)
                .model("WSMP-500")
                .location(LocationEntity.builder()
                        .id(1)
                        .city("Rome")
                        .country("Italy")
                        .build())
                .build();
        SensorEntity sensor2 = SensorEntity.builder()
                .id(2)
                .model("WSMP-500")
                .location(LocationEntity.builder()
                        .id(2)
                        .city("London")
                        .country("United Kingdom")
                        .build())
                .build();

        List<SensorEntity> sensorEntities = List.of(sensor, sensor2);
        Page<SensorEntity> sensorEntitiesPage = new PageImpl<>(sensorEntities);

        SensorResponse sensorResponse = new SensorResponse("WSMP-500", new LocationResponse(1, "Rome", "Italy"), LocalDateTime.now(), "SYSTEM");
        SensorResponse sensorResponse2 = new SensorResponse("WSMP-500", new LocationResponse(2, "London", "United Kingdom"), LocalDateTime.now(), "SYSTEM");

        List<SensorResponse> expectedResponse = List.of(sensorResponse, sensorResponse2);

        when(sensorRepository.findAll(any(Predicate.class), any(Pageable.class))).thenReturn(sensorEntitiesPage);
        when(sensorMapper.sensorEntityToSensorResponse(sensor)).thenReturn(sensorResponse);
        when(sensorMapper.sensorEntityToSensorResponse(sensor2)).thenReturn(sensorResponse2);

        Pageable pageable = PageRequest.of(0, 10);

        List<SensorResponse> actualResponse = sensorService.getAllSensors(new LocationFilter("", ""), pageable);

        Assertions.assertThat(actualResponse).containsExactlyInAnyOrderElementsOf(expectedResponse);

        verify(sensorRepository, times(1)).findAll(any(Predicate.class), any(Pageable.class));
        verify(sensorMapper, times(sensorEntities.size())).sensorEntityToSensorResponse(any(SensorEntity.class));
    }

    @Test
    public void testGetAllSensors_whenFilterIsSet_shouldReturnFilteredSensorList() {
        List<SensorEntity> sensorEntities = IntStream.rangeClosed(1, 3)
                .mapToObj(i -> SensorEntity.builder()
                        .id(i)
                        .model("WSMP-%d".formatted(i))
                        .location(LocationEntity.builder().id(i).city("City #%d".formatted(i)).country("Country").build())
                        .build())
                .toList();

        Page<SensorEntity> sensorEntityPage = new PageImpl<>(sensorEntities);

        List<SensorResponse> expectedResponse = sensorEntities.stream()
                .map(entity -> new SensorResponse(
                        entity.getModel(),
                        new LocationResponse(entity.getLocation().getId(), entity.getLocation().getCity(), entity.getLocation().getCountry()),
                        entity.getCreatedAt(),
                        entity.getCreatedBy()
                ))
                .toList();

        when(sensorRepository.findAll(any(Predicate.class), any(Pageable.class))).thenReturn(sensorEntityPage);
        when(sensorMapper.sensorEntityToSensorResponse(any(SensorEntity.class)))
                .thenAnswer(invocation -> {
                    SensorEntity entity = invocation.getArgument(0);
                    return new SensorResponse(
                            entity.getModel(),
                            new LocationResponse(entity.getLocation().getId(), entity.getLocation().getCity(), entity.getLocation().getCountry()),
                            entity.getCreatedAt(),
                            entity.getCreatedBy()
                    );
                });
        Pageable pageable = PageRequest.of(0, 10);

        List<SensorResponse> actualResponse = sensorService.getAllSensors(new LocationFilter("", "City"), pageable);

        Assertions.assertThat(actualResponse).containsExactlyInAnyOrderElementsOf(expectedResponse);

        verify(sensorRepository, times(1)).findAll(any(Predicate.class), any(Pageable.class));
        verify(sensorMapper, times(sensorEntities.size())).sensorEntityToSensorResponse(any(SensorEntity.class));
    }

    @Test
    public void testGetSensorById_whenSensorExists_returnOptionalWithSensorResponse() {
        SensorEntity sensor = SensorEntity.builder()
                .id(1)
                .model("WSMP-500")
                .location(LocationEntity.builder()
                        .id(1)
                        .city("Rome")
                        .country("Italy")
                        .build())
                .build();
        SensorResponse expectedResponse = new SensorResponse(
                "WSMP-500",
                new LocationResponse(1, "Rome", "Italy"),
                LocalDateTime.now(),
                "SYSTEM");

        when(sensorRepository.findById(SENSOR_ID)).thenReturn(Optional.of(sensor));
        when(sensorMapper.sensorEntityToSensorResponse(sensor)).thenReturn(expectedResponse);

        Optional<SensorResponse> actualSensorOpt = sensorService.getSensorById(SENSOR_ID);

        actualSensorOpt.ifPresent(actual -> {
            Assertions.assertThat(actual).isNotNull();
            Assertions.assertThat(actual).isEqualTo(expectedResponse);
        });

        verify(sensorRepository, times(1)).findById(anyInt());
        verify(sensorMapper, times(1)).sensorEntityToSensorResponse(any(SensorEntity.class));
        verifyNoMoreInteractions(sensorMapper);
        verifyNoMoreInteractions(sensorRepository);
    }

    @Test
    public void testGetSensorById_whenSensorDoesNotExist_shouldReturnEmptyOptional() {
        when(sensorRepository.findById(anyInt())).thenReturn(Optional.empty());

        Optional<SensorResponse> actualSensorOpt = sensorService.getSensorById(SENSOR_ID);

        Assertions.assertThat(actualSensorOpt).isNotPresent();

        verify(sensorRepository, times(1)).findById(SENSOR_ID);
        verifyNoMoreInteractions(sensorRepository);
        verifyNoInteractions(sensorMapper);
    }

    @Test
    public void testAddSensor_whenLocationExists_shouldSaveNewSensor() {
        SensorCreationRequest sensorRequest = new SensorCreationRequest("WSMP-500");
        LocationRequest locationRequest = new LocationRequest("London", "United Kingdom");

        SensorEntity sensorEntity = SensorEntity.builder()
                .model(sensorRequest.model())
                .build();

        LocationResponse locationResponse = new LocationResponse(1, "London", "United Kingdom");

        when(sensorMapper.sensorRequestToSensorEntity(sensorRequest)).thenReturn(sensorEntity);
        when(locationService.getLocationByCityAndCountry(locationRequest)).thenReturn(locationResponse);

        sensorService.addSensor(sensorRequest, locationRequest);

        Assertions.assertThat(sensorEntity.getLocation().getId()).isEqualTo(locationResponse.id());
        Assertions.assertThat(sensorEntity.getLocation().getCity()).isEqualTo(locationResponse.city());
        Assertions.assertThat(sensorEntity.getLocation().getCountry()).isEqualTo(locationResponse.country());

        verify(sensorMapper, times(1)).sensorRequestToSensorEntity(sensorRequest);
        verify(locationService, times(1)).getLocationByCityAndCountry(locationRequest);
        verify(sensorRepository, times(1)).saveAndFlush(sensorEntity);
    }

    @Test
    public void testAddSensor_whenLocationDoesNotExist_shouldThrowLocationNotFoundException() {
        SensorCreationRequest sensorRequest = new SensorCreationRequest("WSMP-500");
        LocationRequest locationRequest = new LocationRequest("NonExistentCity", "NonExistentCountry");

        SensorEntity sensorEntity = SensorEntity.builder()
                .model(sensorRequest.model())
                .build();

        when(locationService.getLocationByCityAndCountry(locationRequest)).thenThrow(LocationNotFoundException.class);

        Assertions.assertThatThrownBy(() -> sensorService.addSensor(sensorRequest, locationRequest))
                .isInstanceOf(LocationNotFoundException.class);

        verify(sensorMapper, never()).sensorRequestToSensorEntity(sensorRequest);
        verify(locationService, times(1)).getLocationByCityAndCountry(locationRequest);
        verify(sensorRepository, never()).saveAndFlush(sensorEntity);
    }

    @Test
    public void testDeleteSensor_whenSensorExists_shouldReturnTrue() {
        SensorEntity sensor = SensorEntity.builder()
                .id(SENSOR_ID)
                .model("WSMP-500")
                .build();

        when(sensorRepository.findById(SENSOR_ID)).thenReturn(Optional.of(sensor));

        boolean actualResult = sensorService.deleteSensor(SENSOR_ID);

        Assertions.assertThat(actualResult).isTrue();

        verify(sensorRepository, times(1)).findById(anyInt());
        verify(sensorRepository, times(1)).delete(any(SensorEntity.class));
    }

    @Test
    public void testDeleteUser_whenUserDoesNotExist_shouldReturnFalse() {
        when(sensorRepository.findById(SENSOR_ID)).thenReturn(Optional.empty());

        boolean actualResult = sensorService.deleteSensor(SENSOR_ID);

        Assertions.assertThat(actualResult).isFalse();

        verify(sensorRepository, times(1)).findById(SENSOR_ID);
        verify(sensorRepository, never()).delete(any(SensorEntity.class));
    }
}
