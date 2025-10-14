package pl.dominik.elearningcenter.application.user.input;

public record AuthenticateUserInput(
        String email,
        String password
) {
}
