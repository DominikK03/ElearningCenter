package pl.dominik.elearningcenter.application.user.command;

public record GetAllUsersCommand(
        int page,
        int size
) {
    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = 100;

    public GetAllUsersCommand {
        if (page < 0) {
            throw new IllegalArgumentException("Page must be >= 0");
        }
        if (size < GetAllUsersCommand.MIN_SIZE || size > GetAllUsersCommand.MAX_SIZE) {
            throw new IllegalArgumentException("Size must be between 1 and 100");
        }
    }
}
