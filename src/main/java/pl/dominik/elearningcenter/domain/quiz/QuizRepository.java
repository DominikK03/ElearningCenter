package pl.dominik.elearningcenter.domain.quiz;

import pl.dominik.elearningcenter.domain.quiz.exception.QuizNotFoundException;

import java.util.List;
import java.util.Optional;

public interface QuizRepository {
    Quiz save(Quiz quiz);

    Optional<Quiz> findById(Long id);

    List<Quiz> findAll();

    Optional<Quiz> findByLessonId(Long lessonId);

    List<Quiz> findByInstructorId(Long instructorId);

    boolean existsById(Long id);

    boolean existsByLessonId(Long lessonId);

    void delete(Quiz quiz);

    default Quiz findByIdOrThrow(Long quizId) {
        return findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found: " + quizId));
    }
}
