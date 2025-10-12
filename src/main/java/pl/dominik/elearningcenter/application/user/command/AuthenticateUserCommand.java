package pl.dominik.elearningcenter.application.user.command;

public record AuthenticateUserCommand(
        String email,
        String password
) {
}
