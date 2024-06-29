package org.project.capstone.weather.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.project.capstone.weather.api.entity.common.AuditingEntity;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(of = "email", callSuper = true)
@Entity
@Table(name = "users")
public class UserEntity extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String firstname;

    private String lastname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserLocationsEntity> userLocations = new ArrayList<>();

}
