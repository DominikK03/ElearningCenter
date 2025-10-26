package pl.dominik.elearningcenter.infrastructure.persistence.enrollment;

import org.springframework.stereotype.Component;
import pl.dominik.elearningcenter.domain.enrollment.CompletedLesson;
import pl.dominik.elearningcenter.domain.enrollment.CompletedLessonRepository;

import java.util.List;
import java.util.Optional;

@Component
public class CompletedLessonRepositoryAdapter implements CompletedLessonRepository {
    private final CompletedLessonJpaRepository jpaRepository;

    public CompletedLessonRepositoryAdapter(CompletedLessonJpaRepository jpaRepository){
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CompletedLesson save(CompletedLesson completedLesson) {
        return jpaRepository.save(completedLesson);
    }

    @Override
    public Optional<CompletedLesson> findByEnrollmentIdAndLessonId(Long enrollmentId, Long lessonId) {
        return jpaRepository.findByEnrollmentIdAndLessonId(enrollmentId, lessonId);
    }

    @Override
    public List<CompletedLesson> findByEnrollmentId(Long enrollmentId) {
        return jpaRepository.findByEnrollmentId(enrollmentId);
    }

    @Override
    public long countByEnrollmentId(Long enrollmentId) {
        return jpaRepository.countByEnrollmentId(enrollmentId);
    }

    @Override
    public boolean existsByEnrollmentIdAndLessonId(Long enrollmentId, Long lessonId) {
        return jpaRepository.existsByEnrollmentIdAndLessonId(enrollmentId, lessonId);
    }
}
