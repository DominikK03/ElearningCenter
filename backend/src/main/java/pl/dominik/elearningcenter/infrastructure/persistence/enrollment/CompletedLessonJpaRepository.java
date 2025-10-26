package pl.dominik.elearningcenter.infrastructure.persistence.enrollment;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.dominik.elearningcenter.domain.enrollment.CompletedLesson;

import java.util.List;
import java.util.Optional;

public interface CompletedLessonJpaRepository extends JpaRepository<CompletedLesson, Long> {
    Optional<CompletedLesson> findByEnrollmentIdAndLessonId(Long enrollmentId, Long lessonId);
    boolean existsByEnrollmentIdAndLessonId(Long enrollmentId, Long lessonId);
    long countByEnrollmentId(Long enrollmentId);
    List<CompletedLesson> findByEnrollmentId(Long enrollmentId);
}
