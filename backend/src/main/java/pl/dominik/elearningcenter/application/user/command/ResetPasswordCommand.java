package pl.dominik.elearningcenter.application.user.command;

public record ResetPasswordCommand(String token, String newPassword) {
}