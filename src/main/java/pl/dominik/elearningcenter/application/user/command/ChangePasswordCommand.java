package pl.dominik.elearningcenter.application.user.command;

public record ChangePasswordCommand(
        Long userId,
        String oldPassword,
        String newPassword
) { }
