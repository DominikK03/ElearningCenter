package pl.dominik.elearningcenter.application.course.command;

public record PublishCourseCommand(Long courseId, Long actorId, boolean isAdmin) {
    public PublishCourseCommand {
        if (courseId == null) throw new IllegalArgumentException("Course ID cannot be null");
        if (actorId == null) throw new IllegalArgumentException("Actor ID cannot be null");
    }
}
