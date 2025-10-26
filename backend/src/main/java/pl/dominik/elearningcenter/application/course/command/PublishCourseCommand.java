package pl.dominik.elearningcenter.application.course.command;

public record PublishCourseCommand(Long courseId, Long instructorId) {
    public PublishCourseCommand {
        if (courseId == null) throw new IllegalArgumentException("Course ID cannot be null");
        if (instructorId == null) throw new IllegalArgumentException("Instructor ID cannot be null");
    }
}
