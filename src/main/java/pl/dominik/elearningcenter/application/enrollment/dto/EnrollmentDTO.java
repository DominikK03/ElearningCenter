package pl.dominik.elearningcenter.application.enrollment.dto;

import pl.dominik.elearningcenter.domain.enrollment.Enrollment;
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
    public static EnrollmentDTO from(Enrollment enrollment){
        return new EnrollmentDTO(
                enrollment.getId(),
                enrollment.getStudentId(),
                enrollment.getCourseId(),
                enrollment.getProgress().getPercentage(),
                enrollment.getEnrolledAt(),
                enrollment.getCompletedAt(),
                enrollment.getStatus()
        );
    }
}
