package pl.dominik.elearningcenter.domain.enrollment;

import pl.dominik.elearningcenter.domain.enrollment.exception.EnrollmentNotFoundException;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository {
    Enrollment save(Enrollment enrollment);

    Optional<Enrollment> findById(Long id);

    List<Enrollment> findAll();

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByCourseId(Long courseId);

    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    void delete(Enrollment enrollment);

    default Enrollment findByIdOrThrow(Long enrollmentId){
        return findById(enrollmentId)
                .orElseThrow(() -> new EnrollmentNotFoundException("Enrollment not found: " + enrollmentId));
    }

}

