package pl.dominik.elearningcenter.application.enrollment.mapper;

import org.springframework.stereotype.Component;
import pl.dominik.elearningcenter.application.enrollment.dto.EnrollmentDTO;
import pl.dominik.elearningcenter.domain.enrollment.Enrollment;

import java.util.List;

@Component
public class EnrollmentMapper {

    public EnrollmentDTO toDto(Enrollment enrollment) {
        if (enrollment == null) {
            throw new IllegalArgumentException("Enrollment cannot be null");
        }

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

    public List<EnrollmentDTO> toDtoList(List<Enrollment> enrollments) {
        if (enrollments == null) {
            throw new IllegalArgumentException("Enrollments list cannot be null");
        }

        return enrollments.stream()
                .map(this::toDto)
                .toList();
    }
}
