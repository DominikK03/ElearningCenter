package pl.dominik.elearningcenter.interfaces.rest.enrollment.request;

public record EnrollStudentRequest(
        Long studentId,
        Long courseId
) {
}
