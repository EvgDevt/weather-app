package org.project.capstone.weather.api.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.project.capstone.weather.api.dto.LocationRequest;
import org.project.capstone.weather.api.dto.LocationResponse;
import org.project.capstone.weather.api.dto.SensorCreationRequest;
import org.project.capstone.weather.api.dto.SensorResponse;
import org.project.capstone.weather.api.dto.filter.LocationFilter;
import org.project.capstone.weather.api.repository.LocationRepository;
import org.project.capstone.weather.api.service.LocationService;
import org.project.capstone.weather.api.service.SensorService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SensorController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SensorControllerTest extends BaseControllerTest {

    private static final Integer SENSOR_ID = 1;

    @MockBean
    private SensorService sensorService;

    @MockBean
    private LocationRepository repository;

    @MockBean
    private LocationService locationService;

    @Test
    public void testGetAllSensors() throws Exception {
        List<SensorResponse> sensorResponses = List.of(
                new SensorResponse("WSMP-500",
                        new LocationResponse(1, "Italy", "Rome"),
                        LocalDateTime.of(2024, 5, 21, 10, 10, 10),
                        "SYSTEM"),
                new SensorResponse("WSMP-500",
                        new LocationResponse(1, "Italy", "Rome"),
                        LocalDateTime.of(2024, 5, 21, 10, 10, 10),
                        "SYSTEM")
        );

        when(sensorService.getAllSensors(any(LocationFilter.class), any(Pageable.class))).thenReturn(sensorResponses);


        mockMvc.perform(get("/weather-api/v1/sensors"))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$").isArray(),
                        jsonPath("$").isNotEmpty()
                );

        verify(sensorService, times(1)).getAllSensors(any(LocationFilter.class), any(Pageable.class));
    }

    @Test
    public void testGetSensorById_whenSensorExists_shouldReturnSensorResponse_andStatusOk() throws Exception {
        SensorResponse sensorResponse = new SensorResponse("WSMP-500",
                new LocationResponse(1, "Italy", "Rome"),
                LocalDateTime.of(2024, 5, 21, 10, 10, 10),
                "SYSTEM");
        when(sensorService.getSensorById(SENSOR_ID)).thenReturn(Optional.of(sensorResponse));

        mockMvc.perform(get("/weather-api/v1/sensors/{sensorId}", SENSOR_ID))
                .andExpectAll(
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                        "model": "WSMP-500",
                                        "location": {
                                            "id": 1,
                                            "city": "Rome",
                                            "country": "Italy"
                                        },
                                        "createdAt": "2024-05-21T10:10:10",
                                        "createdBy": "SYSTEM"
                                }
                                """)
                );

        verify(sensorService, times(1)).getSensorById(SENSOR_ID);
    }

    @Test
    public void testGetSensorById_whenSensorDoesNotExist_shouldReturnSensorResponse_andStatusOk() throws Exception {
        when(sensorService.getSensorById(anyInt())).thenReturn(Optional.empty());

        mockMvc.perform(get("/weather-api/v1/sensors/{sensorId}", SENSOR_ID))
                .andExpectAll(
                        status().isNotFound(),
                        result -> Assertions.assertThat(result.getResolvedException())
                                .isInstanceOf(ResponseStatusException.class)
                                .hasMessage("404 NOT_FOUND")
                );

        verify(sensorService, times(1)).getSensorById(anyInt());
    }

    @Test
    public void testAddSensor_whenNewSensorIsAdded_shouldReturnNoContent() throws Exception {
        SensorCreationRequest sensorCreationRequest = new SensorCreationRequest("WSMP-500");
        LocationRequest locationRequest = new LocationRequest("Italy", "Rome");

        doNothing().when(sensorService).addSensor(sensorCreationRequest, locationRequest);

        mockMvc.perform(post("/weather-api/v1/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "sensor": {
                                        "model": "WSMP-500"
                                    },
                                    "location": {
                                        "city": "Rome",
                                        "country": "Italy"
                                    }
                                }
                                """)
                )
                .andExpectAll(
                        status().isCreated()
                );

        verify(sensorService, times(1)).addSensor(any(SensorCreationRequest.class), any(LocationRequest.class));
    }


    @Test
    public void testDeleteSensor_whenSensorExists_shouldReturnNoContent() throws Exception {
        when(sensorService.deleteSensor(SENSOR_ID)).thenReturn(true);

        mockMvc.perform(delete("/weather-api/v1/sensors/{sensorId}", SENSOR_ID))
                .andExpect(status().isNoContent());

        verify(sensorService, times(1)).deleteSensor(anyInt());
    }

    @Test
    public void testDeleteSensor_whenSensorDoesNotExist_shouldReturnNoContent() throws Exception {
        when(sensorService.deleteSensor(SENSOR_ID)).thenReturn(false);

        mockMvc.perform(delete("/weather-api/v1/sensors/{sensorId}", SENSOR_ID))
                .andExpect(status().isNotFound());

        verify(sensorService, times(1)).deleteSensor(anyInt());
    }
}
