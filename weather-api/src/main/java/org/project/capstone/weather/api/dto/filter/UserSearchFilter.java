package org.project.capstone.weather.api.dto.filter;

import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
public record UserSearchFilter(String email,
                               String lastname,
                               @DateTimeFormat(pattern = "yyyy-MM-dd")
                               LocalDate createdAt) {
}
