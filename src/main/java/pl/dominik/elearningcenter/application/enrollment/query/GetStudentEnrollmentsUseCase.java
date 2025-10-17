package pl.dominik.elearningcenter.application.enrollment.query;

import org.springframework.stereotype.Service;
import pl.dominik.elearningcenter.application.enrollment.dto.EnrollmentDTO;
import pl.dominik.elearningcenter.domain.enrollment.EnrollmentRepository;

import java.util.List;

@Service
public class GetStudentEnrollmentsUseCase {
    private final EnrollmentRepository enrollmentRepository;

    public GetStudentEnrollmentsUseCase(EnrollmentRepository enrollmentRepository){
        this.enrollmentRepository = enrollmentRepository;
    }

    public List<EnrollmentDTO> execute(Long studentId){
        return enrollmentRepository.findByStudentId(studentId)
                .stream()
                .map(EnrollmentDTO::from)
                .toList();
    }
}
