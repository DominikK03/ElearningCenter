package pl.dominik.elearningcenter.application.user.dto;

import pl.dominik.elearningcenter.domain.user.UserRole;

import java.time.LocalDateTime;

public record UserDTO(
        Long id,
        String username,
        String email,
        UserRole role,
        LocalDateTime createdAt,
        boolean enabled
) {
}
