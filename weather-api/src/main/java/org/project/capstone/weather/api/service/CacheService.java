package org.project.capstone.weather.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class CacheService {

    private final CacheManager cacheManager;


    @Scheduled(cron = "1 0 * * * *")
    public void evictAllCachesAtIntervals() {
        String cacheName = Objects.requireNonNull(cacheManager.getCache("cities")).getName();
        Objects.requireNonNull(cacheManager.getCache("cities")).clear();
        log.info("Cache: {} - cleared", cacheName);
    }
}
