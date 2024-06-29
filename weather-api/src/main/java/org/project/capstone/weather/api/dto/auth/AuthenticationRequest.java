package org.project.capstone.weather.api.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {

    @Email(message = "{errors.validation.email}")
    @NotBlank(message = "{errors.validation.email.blank}")
    String email;

    @NotBlank(message = "{errors.validation.password.blank}")
    String password;
}
