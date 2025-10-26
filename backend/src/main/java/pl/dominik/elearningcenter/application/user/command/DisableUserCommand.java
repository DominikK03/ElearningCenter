package pl.dominik.elearningcenter.application.user.command;

public record DisableUserCommand(Long userId) {
    public DisableUserCommand {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
    }
}
