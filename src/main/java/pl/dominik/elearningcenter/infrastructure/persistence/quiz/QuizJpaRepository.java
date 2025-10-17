package pl.dominik.elearningcenter.infrastructure.persistence.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.dominik.elearningcenter.domain.quiz.Quiz;

import java.util.List;
import java.util.Optional;

interface QuizJpaRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByLessonId(Long lessonId);

    List<Quiz> findByInstructorId(Long instructorId);

    boolean existsByLessonId(Long lessonId);
}
