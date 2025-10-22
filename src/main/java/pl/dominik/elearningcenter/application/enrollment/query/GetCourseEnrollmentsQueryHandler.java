package pl.dominik.elearningcenter.application.enrollment.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.enrollment.dto.EnrollmentDTO;
import pl.dominik.elearningcenter.application.enrollment.mapper.EnrollmentMapper;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.enrollment.Enrollment;
import pl.dominik.elearningcenter.domain.enrollment.EnrollmentRepository;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

import java.util.List;

@Service
public class GetCourseEnrollmentsQueryHandler {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentMapper enrollmentMapper;

    public GetCourseEnrollmentsQueryHandler(
            EnrollmentRepository enrollmentRepository,
            CourseRepository courseRepository,
            EnrollmentMapper enrollmentMapper
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentMapper = enrollmentMapper;
    }

    @Transactional(readOnly = true)
    public List<EnrollmentDTO> handle(GetCourseEnrollmentsQuery query) {
        Course course = courseRepository.findByIdOrThrow(query.courseId());

        if (!course.isOwnedBy(query.instructorId())) {
            throw new DomainException("Only course owner can view enrollments");
        }

        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(query.courseId());
        return enrollmentMapper.toDtoList(enrollments);
    }
}
