package pl.dominik.elearningcenter.application.enrollment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.enrollment.dto.EnrollmentDTO;
import pl.dominik.elearningcenter.application.enrollment.input.UpdateProgressInput;
import pl.dominik.elearningcenter.domain.enrollment.Enrollment;
import pl.dominik.elearningcenter.domain.enrollment.EnrollmentRepository;
import pl.dominik.elearningcenter.domain.enrollment.exception.EnrollmentNotFoundException;

@Service
public class UpdateProgressUseCase {
    private final EnrollmentRepository enrollmentRepository;

    public UpdateProgressUseCase(EnrollmentRepository enrollmentRepository){
        this.enrollmentRepository = enrollmentRepository;
    }

    @Transactional
    public EnrollmentDTO execute(UpdateProgressInput command){
        Enrollment enrollment = enrollmentRepository.findById(command.enrollmentId())
                .orElseThrow(() -> new EnrollmentNotFoundException("Enrollment not found: " + command.enrollmentId()));

        enrollment.updateProgress(command.percentage());
        return EnrollmentDTO.from(enrollment);
    }
}
