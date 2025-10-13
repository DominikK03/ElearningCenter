package pl.dominik.elearningcenter.application.user.dto;

import pl.dominik.elearningcenter.domain.user.User;
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
    public static UserDTO from(User user){
        return new UserDTO(
                user.getId(),
                user.getUsername().getValue(),
                user.getEmail().getValue(),
                user.getRole(),
                user.getCreatedAt(),
                user.isEnabled()
        );
    }
}
