package pl.dominik.elearningcenter.application.user.query;

public record GetUserByIdQuery(Long userId) {
    public GetUserByIdQuery {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
    }
}
