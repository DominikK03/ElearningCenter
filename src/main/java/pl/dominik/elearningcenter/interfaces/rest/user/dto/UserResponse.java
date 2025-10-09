package pl.dominik.elearningcenter.interfaces.rest.user.dto;

import pl.dominik.elearningcenter.application.user.dto.UserDTO;
import pl.dominik.elearningcenter.domain.user.UserRole;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String email,
        UserRole role,
        LocalDateTime createdAt,
        boolean enabled
) {
    public static UserResponse from(UserDTO dto) {
        return new UserResponse(
                dto.id(),
                dto.username(),
                dto.email(),
                dto.role(),
                dto.createdAt(),
                dto.enabled()
        );
    }
}
