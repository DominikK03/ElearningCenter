package pl.dominik.elearningcenter.application.course.command;

public record DeleteCourseCommand(
        Long courseId,
        Long actorId,
        boolean isAdmin,
        String reason
) {
    public DeleteCourseCommand {
        if (courseId == null) throw new IllegalArgumentException("Course ID cannot be null");
        if (actorId == null) throw new IllegalArgumentException("Actor ID cannot be null");
    }
}
