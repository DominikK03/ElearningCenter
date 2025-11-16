package pl.dominik.elearningcenter.domain.quiz;

import pl.dominik.elearningcenter.domain.quiz.exception.QuizAccessDeniedException;
import pl.dominik.elearningcenter.domain.quiz.exception.QuizNotFoundException;

import java.util.List;
import java.util.Optional;

public interface QuizRepository {
    Quiz save(Quiz quiz);

    Optional<Quiz> findById(Long id);

    Optional<Quiz> findByIdAndInstructorId(Long id, Long instructorId);

    List<Quiz> findAll();

    Optional<Quiz> findByLessonId(Long lessonId);

    List<Quiz> findByInstructorId(Long instructorId);

    List<Quiz> findByCourseId(Long courseId);

    List<Quiz> findBySectionId(Long sectionId);

    boolean existsById(Long id);

    boolean existsByLessonId(Long lessonId);

    boolean existsByCourseIdOnly(Long courseId);

    boolean existsBySectionIdOnly(Long sectionId);

    Optional<Quiz> findByCourseIdOnly(Long courseId);

    Optional<Quiz> findBySectionIdOnly(Long sectionId);

    void delete(Quiz quiz);

    default Quiz findByIdOrThrow(Long quizId) {
        return findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found: " + quizId));
    }

    default Quiz findByIdAndInstructorIdOrThrow(Long quizId, Long instructorId) {
        return findByIdAndInstructorId(quizId, instructorId)
                .orElseThrow(() -> {
                    if (!existsById(quizId)) {
                        return new QuizNotFoundException("Quiz not found: " + quizId);
                    }
                    return new QuizAccessDeniedException("Access denied to quiz: " + quizId);
                });
    }
}
