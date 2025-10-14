package pl.dominik.elearningcenter.application.enrollment.input;

public record EnrollStudentInput(
        Long studentId,
        Long courseId
) {
}
