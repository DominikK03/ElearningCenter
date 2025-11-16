package pl.dominik.elearningcenter.application.enrollment.command;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.enrollment.dto.EnrollmentDTO;
import pl.dominik.elearningcenter.application.enrollment.mapper.EnrollmentMapper;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.exception.CourseNotFoundException;
import pl.dominik.elearningcenter.domain.course.exception.CourseNotPublishedException;
import pl.dominik.elearningcenter.domain.enrollment.Enrollment;
import pl.dominik.elearningcenter.domain.enrollment.EnrollmentRepository;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;
import pl.dominik.elearningcenter.domain.shared.valueobject.Money;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;

@Service
public class EnrollStudentCommandHandler {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentMapper enrollmentMapper;

    public EnrollStudentCommandHandler(
            EnrollmentRepository enrollmentRepository,
            CourseRepository courseRepository,
            UserRepository userRepository,
            EnrollmentMapper enrollmentMapper
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentMapper = enrollmentMapper;
    }

    @Transactional
    public EnrollmentDTO handle(EnrollStudentCommand command) {
        Course course = courseRepository.findById(command.courseId())
                .orElseThrow(() -> new CourseNotFoundException("Course not found: " + command.courseId()));

        if (!course.isPublished()) {
            throw new CourseNotPublishedException("Cannot enroll in unpublished course");
        }

        if (enrollmentRepository.existsByStudentIdAndCourseId(command.studentId(), command.courseId())) {
            throw new DomainException("Student is already enrolled in this course");
        }

        if (course.getPrice().getAmount().compareTo(BigDecimal.ZERO) > 0) {
            User student = userRepository.findByIdOrThrow(command.studentId());

            if (!student.hasEnoughBalance(course.getPrice())) {
                throw new DomainException("Insufficient balance. Course price: " + course.getPrice().getAmount() + " PLN");
            }

            student.deductBalance(course.getPrice());
            userRepository.save(student);

            Long instructorId = course.getInstructorId();
            if (instructorId != null && !instructorId.equals(student.getId())) {
                User instructor = userRepository.findByIdOrThrow(instructorId);
                Money instructorShare = course.getPrice().multiply(BigDecimal.valueOf(0.9));
                instructor.addBalance(instructorShare);
                userRepository.save(instructor);
            }
        }

        Enrollment enrollment = Enrollment.enroll(command.studentId(), command.courseId());
        enrollmentRepository.save(enrollment);

        return enrollmentMapper.toDto(enrollment);
    }
}
