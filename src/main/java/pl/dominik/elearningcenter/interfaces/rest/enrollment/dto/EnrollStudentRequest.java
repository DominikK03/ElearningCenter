package pl.dominik.elearningcenter.interfaces.rest.enrollment.dto;

public record EnrollStudentRequest(
        Long studentId,
        Long courseId
) {
}
