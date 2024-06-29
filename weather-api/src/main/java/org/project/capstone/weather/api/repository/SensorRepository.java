package org.project.capstone.weather.api.repository;

import com.querydsl.core.types.Predicate;
import jakarta.annotation.Nullable;
import org.project.capstone.weather.api.entity.SensorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface SensorRepository extends JpaRepository<SensorEntity, Integer>, QuerydslPredicateExecutor<SensorEntity> {

    @SuppressWarnings("NullableProblems")
    @EntityGraph(attributePaths = {"location"})
    @Override
    Page<SensorEntity> findAll(@Nullable Predicate predicate, @Nullable Pageable pageable);
}
