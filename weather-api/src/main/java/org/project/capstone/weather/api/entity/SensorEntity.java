package org.project.capstone.weather.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.project.capstone.weather.api.entity.common.AuditingEntity;

@Entity
@Table(name = "sensors")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(of = "id", callSuper = true)
public class SensorEntity extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String model;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private LocationEntity location;
}
