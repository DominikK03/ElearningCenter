package pl.dominik.elearningcenter.application.user.command;

public record UpdateUserProfileInput(
        Long userId,
        String newEmail,
        String newUsername
) {
}
