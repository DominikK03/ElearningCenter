package pl.dominik.elearningcenter.application.user.command;

public record ChangePasswordInput(
        Long userId,
        String oldPassword,
        String newPassword
) { }
