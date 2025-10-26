package pl.dominik.elearningcenter.interfaces.rest.enrollment.response;

import pl.dominik.elearningcenter.application.enrollment.dto.EnrollmentDTO;
import pl.dominik.elearningcenter.domain.enrollment.EnrollmentStatus;

import java.time.LocalDateTime;

public record EnrollmentResponse(
        Long id,
        Long studentId,
        Long courseId,
        Integer progressPercentage,
        LocalDateTime enrolledAt,
        LocalDateTime completedAt,
        EnrollmentStatus status
) {
    public static EnrollmentResponse from(EnrollmentDTO dto){
        return new EnrollmentResponse(
                dto.id(),
                dto.studentId(),
                dto.courseId(),
                dto.progressPercentage(),
                dto.enrolledAt(),
                dto.completedAt(),
                dto.status()
        );
    }
}
