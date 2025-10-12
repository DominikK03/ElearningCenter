package pl.dominik.elearningcenter.application.user.command;

public record UpdateUserProfileCommand(
        Long userId,
        String newEmail,
        String newUsername
) {
}
