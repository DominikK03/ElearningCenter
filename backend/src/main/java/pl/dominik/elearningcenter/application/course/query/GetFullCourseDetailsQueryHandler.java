package pl.dominik.elearningcenter.application.course.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.course.dto.CourseDTO;
import pl.dominik.elearningcenter.application.course.mapper.CourseMapper;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.enrollment.EnrollmentRepository;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class GetFullCourseDetailsQueryHandler {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final EnrollmentRepository enrollmentRepository;

    public GetFullCourseDetailsQueryHandler(
            CourseRepository courseRepository,
            CourseMapper courseMapper,
            EnrollmentRepository enrollmentRepository
    ) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
        this.enrollmentRepository = enrollmentRepository;
    }

    public Optional<CourseDTO> handle(GetFullCourseDetailsQuery query) {
        Optional<Course> courseOpt = courseRepository.findWithSectionsById(query.courseId());

        if (courseOpt.isEmpty()) {
            return Optional.empty();
        }

        Course course = courseOpt.get();

        if (!canAccess(course, query.requestingUserId())) {
            return Optional.empty();
        }

        CourseDTO dto = courseMapper.toDto(course);
        return Optional.of(dto);
    }

    private boolean canAccess(Course course, Long userId) {
        boolean isInstructor = course.getInstructorId().equals(userId);
        boolean isEnrolledStudent = enrollmentRepository.existsByStudentIdAndCourseId(userId, course.getId());
        return isInstructor || isEnrolledStudent;
    }
}
