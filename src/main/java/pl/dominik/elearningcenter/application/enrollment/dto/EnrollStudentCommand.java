package pl.dominik.elearningcenter.application.enrollment.dto;

public record EnrollStudentCommand(
        Long studentId,
        Long courseId
) {
}
