package pl.dominik.elearningcenter.application.user.query;

public record GetAllUsersQuery(
        int page,
        int size
) {
    public GetAllUsersQuery {
        if (page < 0) {
            throw new IllegalArgumentException("Page cannot be negative");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        }
    }
}
