package pl.dominik.elearningcenter.application.enrollment.query;

public record GetStudentEnrollmentsQuery(Long studentId) {
    public GetStudentEnrollmentsQuery {
        if (studentId == null) {
            throw new IllegalArgumentException("Student ID cannot be null");
        }
    }
}
