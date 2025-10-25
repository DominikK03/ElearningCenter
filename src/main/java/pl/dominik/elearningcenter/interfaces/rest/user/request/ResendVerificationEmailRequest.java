package pl.dominik.elearningcenter.interfaces.rest.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendVerificationEmailRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email
) {
}