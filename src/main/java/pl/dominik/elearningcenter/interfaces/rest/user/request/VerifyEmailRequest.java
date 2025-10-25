package pl.dominik.elearningcenter.interfaces.rest.user.request;

import jakarta.validation.constraints.NotBlank;

public record VerifyEmailRequest(
        @NotBlank(message = "Token is required")
        String token
) {
}