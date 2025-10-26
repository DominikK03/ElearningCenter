package pl.dominik.elearningcenter.application.enrollment.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.domain.enrollment.Enrollment;
import pl.dominik.elearningcenter.domain.enrollment.EnrollmentRepository;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

@Service
public class UnenrollStudentCommandHandler {
    private final EnrollmentRepository enrollmentRepository;

    public UnenrollStudentCommandHandler(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    @Transactional
    public void handle(UnenrollStudentCommand command) {
        Enrollment enrollment = enrollmentRepository.findByIdOrThrow(command.enrollmentId());

        if (!enrollment.belongsToStudent(command.studentId())) {
            throw new DomainException("You can only unenroll yourself");
        }

        enrollment.drop();
        enrollmentRepository.delete(enrollment);
    }
}