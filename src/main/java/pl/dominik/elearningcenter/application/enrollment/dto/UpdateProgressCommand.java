package pl.dominik.elearningcenter.application.enrollment.dto;

public record UpdateProgressCommand(
        Long enrollmentId,
        int percentage
) {
}
