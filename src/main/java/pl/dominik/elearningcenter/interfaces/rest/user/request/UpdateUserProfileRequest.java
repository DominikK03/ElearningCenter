package pl.dominik.elearningcenter.interfaces.rest.user.request;

public record UpdateUserProfileRequest(
        String email,
        String username
) {
}
