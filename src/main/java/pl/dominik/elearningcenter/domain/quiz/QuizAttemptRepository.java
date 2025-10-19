package pl.dominik.elearningcenter.domain.quiz;

import pl.dominik.elearningcenter.domain.quiz.exception.QuizAttemptNotFoundException;

import java.util.List;
import java.util.Optional;

public interface QuizAttemptRepository {
    QuizAttempt save(QuizAttempt quizAttempt);

    Optional<QuizAttempt> findById(Long id);

    List<QuizAttempt> findAll();

    List<QuizAttempt> findByQuizId(Long quizId);

    List<QuizAttempt> findByStudentId(Long studentId);

    List<QuizAttempt> findByQuizIdAndStudentId(Long quizId, Long studentId);

    Optional<QuizAttempt> findBestAttempt(Long quizId, Long studentId);

    boolean existsByQuizId(Long quizId);

    boolean existsByQuizIdAndStudentId(Long quizId, Long studentId);

    long countByQuizId(Long quizId);

    void delete(QuizAttempt quizAttempt);

    void deleteByQuizId(Long quizId);

    default QuizAttempt findByIdOrThrow(Long attemptId) {
        return findById(attemptId)
                .orElseThrow(() -> new QuizAttemptNotFoundException("Quiz attempt not found: " + attemptId));
    }
}
