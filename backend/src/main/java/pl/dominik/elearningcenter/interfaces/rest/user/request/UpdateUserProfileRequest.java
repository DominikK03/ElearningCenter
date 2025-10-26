package pl.dominik.elearningcenter.interfaces.rest.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequest(
        @Email(message = "Email must be valid")
        String email,

        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username
) {
}
