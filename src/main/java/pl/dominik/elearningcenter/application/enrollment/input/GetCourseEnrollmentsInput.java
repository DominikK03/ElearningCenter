package pl.dominik.elearningcenter.application.enrollment.input;

public record GetCourseEnrollmentsInput(Long courseId, Long instructorId) {
    public GetCourseEnrollmentsInput {
        if (courseId == null) {
            throw new IllegalArgumentException("Course ID cannot be null");
        }
        if (instructorId == null) {
            throw new IllegalArgumentException("Instructor ID cannot be null");
        }
    }
}
