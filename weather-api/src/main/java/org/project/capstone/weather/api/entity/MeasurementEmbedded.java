package org.project.capstone.weather.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.capstone.weather.api.util.converter.WindDirectionConverter;

import java.time.LocalDateTime;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeasurementEmbedded {

    private Double temperature;

    @Column(name = "wind_speed")
    private Double windSpeed;

    @Column(name = "wind_direction")
    @Convert(converter = WindDirectionConverter.class)
    private WindDirection windDirection;

    @Column(name = "humidity")
    private Double humidity;

    @Column(name = "description")
    @Enumerated(EnumType.STRING)
    private WeatherCondition weatherCondition;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id")
    private SensorEntity sensor;
}
