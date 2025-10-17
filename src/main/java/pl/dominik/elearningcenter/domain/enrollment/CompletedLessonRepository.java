package pl.dominik.elearningcenter.domain.enrollment;

import java.util.List;
import java.util.Optional;

public interface CompletedLessonRepository {
    CompletedLesson save(CompletedLesson completedLesson);
    Optional<CompletedLesson> findByEnrollmentIdAndLessonId(Long enrollmentId, Long lessonId);
    List<CompletedLesson> findByEnrollmentId(Long enrollmentId);
    long countByEnrollmentId(Long enrollmentId);
    boolean existsByEnrollmentIdAndLessonId(Long enrollmentId, Long lessonId);
}
