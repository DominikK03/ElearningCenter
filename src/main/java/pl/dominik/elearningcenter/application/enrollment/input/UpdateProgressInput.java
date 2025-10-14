package pl.dominik.elearningcenter.application.enrollment.input;

public record UpdateProgressInput(
        Long enrollmentId,
        int percentage
) {
}
