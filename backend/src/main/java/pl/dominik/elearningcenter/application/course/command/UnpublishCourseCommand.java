package pl.dominik.elearningcenter.application.course.command;

public record UnpublishCourseCommand(Long courseId, Long actorId, boolean isAdmin, String reason) {
    public UnpublishCourseCommand {
        if (courseId == null) throw new IllegalArgumentException("Course ID cannot be null");
        if (actorId == null) throw new IllegalArgumentException("Actor ID cannot be null");
    }
}
