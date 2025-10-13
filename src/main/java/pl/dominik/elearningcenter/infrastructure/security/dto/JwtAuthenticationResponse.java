package pl.dominik.elearningcenter.infrastructure.security.dto;

import pl.dominik.elearningcenter.application.user.dto.UserDTO;
import pl.dominik.elearningcenter.domain.user.UserRole;

public record JwtAuthenticationResponse(
        String token,
        String type,
        Long userId,
        String email,
        UserRole role,
        long expiresIn
) {
    public static JwtAuthenticationResponse create(String token, UserDTO user, long expiresIn){
        return new JwtAuthenticationResponse(
                token,
                "Bearer",
                user.id(),
                user.email(),
                user.role(),
                expiresIn
        );
    }
}
