package pl.dominik.elearningcenter.infrastructure.persistence.enrollment;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.dominik.elearningcenter.domain.enrollment.Enrollment;

import java.util.List;
import java.util.Optional;

interface EnrollmentJpaRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByCourseId(Long courseId);

    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
}
