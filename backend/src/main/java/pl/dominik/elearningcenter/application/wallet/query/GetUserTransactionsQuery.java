package pl.dominik.elearningcenter.application.wallet.query;

public record GetUserTransactionsQuery(Long userId, int page, int size) {
    public GetUserTransactionsQuery {
        if (userId == null) {
            throw new IllegalArgumentException("User id cannot be null");
        }
        if (page < 0) {
            throw new IllegalArgumentException("Page must be >= 0");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Size must be between 1 and 100");
        }
    }
}
