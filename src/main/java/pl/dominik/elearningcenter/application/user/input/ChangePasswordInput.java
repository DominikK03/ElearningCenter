package pl.dominik.elearningcenter.application.user.input;

public record ChangePasswordInput(
        Long userId,
        String oldPassword,
        String newPassword
) { }
