package org.project.capstone.weather.api.repository;

import com.querydsl.core.types.Predicate;
import org.project.capstone.weather.api.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

@SuppressWarnings("NullableProblems")
public interface UserRepository extends JpaRepository<UserEntity, Integer>, QuerydslPredicateExecutor<UserEntity> {

    Optional<UserEntity> findByEmail(String email);

    @EntityGraph(attributePaths = {"userLocations"})
    Page<UserEntity> findAll(Predicate predicate, Pageable pageable);

}
