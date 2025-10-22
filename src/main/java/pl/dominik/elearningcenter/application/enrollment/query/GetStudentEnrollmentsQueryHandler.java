package pl.dominik.elearningcenter.application.enrollment.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.enrollment.dto.EnrollmentDTO;
import pl.dominik.elearningcenter.application.enrollment.mapper.EnrollmentMapper;
import pl.dominik.elearningcenter.domain.enrollment.Enrollment;
import pl.dominik.elearningcenter.domain.enrollment.EnrollmentRepository;

import java.util.List;

@Service
public class GetStudentEnrollmentsQueryHandler {
    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentMapper enrollmentMapper;

    public GetStudentEnrollmentsQueryHandler(
            EnrollmentRepository enrollmentRepository,
            EnrollmentMapper enrollmentMapper
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.enrollmentMapper = enrollmentMapper;
    }

    @Transactional(readOnly = true)
    public List<EnrollmentDTO> handle(GetStudentEnrollmentsQuery query) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(query.studentId());
        return enrollmentMapper.toDtoList(enrollments);
    }
}
