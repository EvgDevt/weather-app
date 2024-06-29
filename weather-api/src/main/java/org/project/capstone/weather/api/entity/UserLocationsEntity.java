package org.project.capstone.weather.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLocationsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private LocationEntity location;

    public void setUser(UserEntity user) {
        this.user = user;
        this.user.getUserLocations().add(this);
    }

    public void setLocation(LocationEntity location) {
        this.location = location;
        this.location.getUserLocations().add(this);
    }
}
