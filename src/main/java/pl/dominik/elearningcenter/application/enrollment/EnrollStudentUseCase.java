package pl.dominik.elearningcenter.application.enrollment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.enrollment.dto.EnrollStudentCommand;
import pl.dominik.elearningcenter.application.enrollment.dto.EnrollmentDTO;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.exception.CourseNotFoundException;
import pl.dominik.elearningcenter.domain.course.exception.CourseNotPublishedException;
import pl.dominik.elearningcenter.domain.enrollment.Enrollment;
import pl.dominik.elearningcenter.domain.enrollment.EnrollmentRepository;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

@Service
public class EnrollStudentUseCase {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    public EnrollStudentUseCase(EnrollmentRepository enrollmentRepository, CourseRepository courseRepository){
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public EnrollmentDTO execute(EnrollStudentCommand command){
        Course course = courseRepository.findById(command.courseId())
                .orElseThrow(() -> new CourseNotFoundException("Course not found: " + command.courseId()));
        if (!course.isPublished()){
            throw new CourseNotPublishedException("Cannot enroll in unpublished course");
        }
        if (enrollmentRepository.existsByStudentIdAndCourseId(command.studentId(), command.courseId())){
            throw new DomainException("Student is already enrolled in this course");
        }
        Enrollment enrollment = Enrollment.enroll(command.studentId(), command.courseId());
        enrollmentRepository.save(enrollment);

        return EnrollmentDTO.from(enrollment);
    }
}
