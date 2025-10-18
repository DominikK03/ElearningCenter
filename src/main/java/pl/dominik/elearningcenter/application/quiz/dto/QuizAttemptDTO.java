package pl.dominik.elearningcenter.application.quiz.dto;

import pl.dominik.elearningcenter.domain.quiz.QuizAttempt;

import java.time.LocalDateTime;
import java.util.List;

public record QuizAttemptDTO(
        Long id,
        Long quizId,
        Long studentId,
        int score,
        int maxScore,
        int scorePercentage,
        boolean passed,
        LocalDateTime attemptedAt,
        List<StudentAnswerDTO> answers
) {
    public static QuizAttemptDTO from(QuizAttempt attempt) {
        return new QuizAttemptDTO(
                attempt.getId(),
                attempt.getQuizId(),
                attempt.getStudentId(),
                attempt.getScore(),
                attempt.getMaxScore(),
                attempt.getScorePercentage(),
                attempt.isPassed(),
                attempt.getAttemptedAt(),
                attempt.getAnswers().stream()
                        .map(StudentAnswerDTO::from)
                        .toList()
        );
    }
}
