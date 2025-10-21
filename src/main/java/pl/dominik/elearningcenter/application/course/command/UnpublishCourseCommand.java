package pl.dominik.elearningcenter.application.course.command;

public record UnpublishCourseCommand(Long courseId, Long instructorId) {
    public UnpublishCourseCommand {
        if (courseId == null) throw new IllegalArgumentException("Course ID cannot be null");
        if (instructorId == null) throw new IllegalArgumentException("Instructor ID cannot be null");
    }

}
