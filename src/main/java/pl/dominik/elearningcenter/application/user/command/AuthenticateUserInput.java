package pl.dominik.elearningcenter.application.user.command;

public record AuthenticateUserInput(
        String email,
        String password
) {
}
