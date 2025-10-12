package pl.dominik.elearningcenter.application.course.command;

import pl.dominik.elearningcenter.application.user.command.GetAllUsersCommand;

public record GetAllCoursesCommand(
        int page,
        int size
) {
    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = 100;

    public GetAllCoursesCommand{
        if (page < 0){
            throw new IllegalArgumentException("Page must be >= 0");
        }
        if (size < MIN_SIZE || size > MAX_SIZE){
            throw new IllegalArgumentException("Size must be between 1 and 100");
        }
    }
}
