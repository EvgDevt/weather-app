package org.project.capstone.weather.api.dto.auth;

import lombok.Builder;
import lombok.With;

@Builder
@With
public record AuthenticationResponse(String token) {
}
