package org.project.capstone.weather.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.project.capstone.weather.api.entity.Role;
import org.project.capstone.weather.api.validation.CreateAction;
import org.project.capstone.weather.api.validation.ValidPassword;

public record UserCreateEditRequest(
        @NotBlank(message = "{errors.validation.email.blank}")
        @Email(message = "{errors.validation.email}")
        String email,

        @ValidPassword(groups = {CreateAction.class})
        String password,

        @Size(min = 1, max = 12, message = "{errors.validation.firstname.length}")
        @NotBlank(message = "{errors.validation.firstname.blank}")
        String firstname,

        @Size(min = 1, max = 16, message = "{errors.validation.lastname.length}")
        @NotBlank(message = "{errors.validation.lastname.blank}")
        String lastname,

        @NotNull(message = "{errors.validation.role.null}")
        Role role) {
}
