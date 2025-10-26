package pl.dominik.elearningcenter.application.user.query;

public record GetCurrentUserQuery(Long userId) {
    public GetCurrentUserQuery {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
    }
}