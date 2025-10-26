package pl.dominik.elearningcenter.application.user.command;

public record EnableUserCommand(Long userId) {
    public EnableUserCommand {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
    }
}
