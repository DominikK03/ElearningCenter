package pl.dominik.elearningcenter.interfaces.rest.user.request;

public record ChangePasswordRequest(
        String oldPassword,
        String newPassword
) {
}
