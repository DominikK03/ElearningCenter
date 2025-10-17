package pl.dominik.elearningcenter.application.enrollment.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.enrollment.dto.EnrollmentDTO;
import pl.dominik.elearningcenter.application.enrollment.input.GetCourseEnrollmentsInput;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.enrollment.Enrollment;
import pl.dominik.elearningcenter.domain.enrollment.EnrollmentRepository;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

import java.util.List;

@Service
public class GetCourseEnrollmentsUseCase {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    public GetCourseEnrollmentsUseCase(
            EnrollmentRepository enrollmentRepository,
            CourseRepository courseRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional(readOnly = true)
    public List<EnrollmentDTO> execute(GetCourseEnrollmentsInput command) {
        Course course = courseRepository.findByIdOrThrow(command.courseId());

        if (!course.isOwnedBy(command.instructorId())) {
            throw new DomainException("Only course owner can view enrollments");
        }

        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(command.courseId());

        return enrollments.stream()
                .map(EnrollmentDTO::from)
                .toList();
    }
}
