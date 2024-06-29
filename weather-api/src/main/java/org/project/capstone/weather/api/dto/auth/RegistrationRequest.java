package org.project.capstone.weather.api.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegistrationRequest(
        @NotBlank(message = "{errors.validation.email.blank}")
        @Email(message = "{errors.validation.email}")
        String email,

        @Size(min = 8, max = 16, message = "{errors.validation.password.length}")
        @NotBlank(message = "{errors.validation.password.blank}")
        @NotNull(message = "{errors.validation.password.absence}")
        String password,

        @Size(min = 1, max = 12, message = "{errors.validation.firstname.length}")
        @NotBlank(message = "{errors.validation.firstname.blank}")
        String firstname,

        @Size(min = 1, max = 16, message = "{errors.validation.lastname.length}")
        @NotBlank(message = "{errors.validation.lastname.blank}")
        String lastname) {
}
