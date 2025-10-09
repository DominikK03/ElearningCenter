package pl.dominik.elearningcenter.application.user.dto;

public record AuthenticateUserCommand(
        String email,
        String password
) {
}
