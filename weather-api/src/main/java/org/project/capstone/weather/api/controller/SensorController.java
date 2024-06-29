package org.project.capstone.weather.api.controller;

import lombok.RequiredArgsConstructor;
import org.project.capstone.weather.api.dto.SensorCreationWrapper;
import org.project.capstone.weather.api.dto.SensorResponse;
import org.project.capstone.weather.api.dto.filter.LocationFilter;
import org.project.capstone.weather.api.service.SensorService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/weather-api/v1/sensors")
public class SensorController {

    private final SensorService sensorService;


    @GetMapping
    public List<SensorResponse> getAllSensors(LocationFilter filter, Pageable pageable) {
        return sensorService.getAllSensors(filter, pageable);
    }

    @GetMapping("/{sensorId}")
    public SensorResponse getSensorById(@PathVariable("sensorId") Integer id) {
        return sensorService.getSensorById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addSensor(@RequestBody SensorCreationWrapper request) {
        sensorService.addSensor(request.sensor(), request.location());
    }

    @DeleteMapping("/{sensorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSensor(@PathVariable("sensorId") Integer id) {
        if (!sensorService.deleteSensor(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
