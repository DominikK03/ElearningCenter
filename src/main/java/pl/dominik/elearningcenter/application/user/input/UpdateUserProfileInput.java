package pl.dominik.elearningcenter.application.user.input;

public record UpdateUserProfileInput(
        Long userId,
        String newEmail,
        String newUsername
) {
}
