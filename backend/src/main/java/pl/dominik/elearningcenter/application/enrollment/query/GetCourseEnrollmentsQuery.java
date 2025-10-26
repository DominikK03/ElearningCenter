package pl.dominik.elearningcenter.application.enrollment.query;

public record GetCourseEnrollmentsQuery(Long courseId, Long instructorId) {
    public GetCourseEnrollmentsQuery {
        if (courseId == null) {
            throw new IllegalArgumentException("Course ID cannot be null");
        }
        if (instructorId == null) {
            throw new IllegalArgumentException("Instructor ID cannot be null");
        }
    }
}
