package pl.dominik.elearningcenter.application.enrollment.dto;

import pl.dominik.elearningcenter.domain.enrollment.EnrollmentStatus;

import java.time.LocalDateTime;

public record EnrollmentDTO(
        Long id,
        Long studentId,
        Long courseId,
        Integer progressPercentage,
        LocalDateTime enrolledAt,
        LocalDateTime completedAt,
        EnrollmentStatus status
) {
}
