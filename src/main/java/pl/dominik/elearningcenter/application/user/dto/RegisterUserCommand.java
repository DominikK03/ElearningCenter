package pl.dominik.elearningcenter.application.user.dto;

import pl.dominik.elearningcenter.domain.user.UserRole;

public record RegisterUserCommand(
        String username,
        String email,
        String password,
        UserRole role
) {
}
