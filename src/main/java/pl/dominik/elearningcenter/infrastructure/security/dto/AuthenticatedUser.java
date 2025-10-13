package pl.dominik.elearningcenter.infrastructure.security.dto;

import pl.dominik.elearningcenter.domain.user.UserRole;

public record AuthenticatedUser(
        Long userId,
        String email,
        UserRole role
) {
    public boolean hasRole(UserRole role) {
        return this.role == role;
    }

    public boolean isInstructor() {
        return role == UserRole.INSTRUCTOR;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }
}
