package pl.dominik.elearningcenter.application.user.query;

public record AuthenticateUserQuery(
        String email,
        String password
) {
}
