package pl.dominik.elearningcenter.application.enrollment.command;

public record EnrollStudentCommand(
        Long studentId,
        Long courseId
) {
}
