package pl.dominik.elearningcenter.application.course.input;

public record UnpublishCourseInput(Long courseId, Long instructorId) {
    public UnpublishCourseInput {
        if (courseId == null) throw new IllegalArgumentException("Course ID cannot be null");
        if (instructorId == null) throw new IllegalArgumentException("Instructor ID cannot be null");
    }

}
