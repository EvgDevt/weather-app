package org.project.capstone.weather.api.repository;

import org.project.capstone.weather.api.entity.WeatherEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WeatherRepository extends JpaRepository<WeatherEntity, Integer> {

    String WEATHER_BY_CITIES_QUERY = """
            SELECT DISTINCT ON (location_id) *
            FROM weather_data
            WHERE location_id IN (
                    SELECT id FROM locations
                    WHERE city IN (:cities))
            ORDER BY location_id, created_at DESC;
            """;


    @Query("SELECT w FROM WeatherEntity w JOIN FETCH w.location l WHERE lower(l.city) = lower(:city) ORDER BY w.measurement.createdAt DESC LIMIT 1")
    @Cacheable(value = "cities", unless = "#result == null ")
    Optional<WeatherEntity> findLatestWeatherByCity(@Param("city") String city);

    @Query(
            value = WEATHER_BY_CITIES_QUERY,
            nativeQuery = true
    )
    List<WeatherEntity> findWeatherByCities(@Param("cities") List<String> cities);

    @Query("SELECT w FROM WeatherEntity w " +
           "JOIN FETCH w.location l " +
           "WHERE lower(l.city) = lower(:city) " +
           "AND w.measurement.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY w.measurement.createdAt DESC ")
    @Cacheable("cityWithDateRange")
    List<WeatherEntity> findByCityAndDateRange(@Param("city") String city,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDAte);

    @EntityGraph(attributePaths = {"location"})
    List<WeatherEntity> findAllByLocationCityIgnoreCaseOrderByMeasurementCreatedAtDesc(String city, Pageable pageable);
}
