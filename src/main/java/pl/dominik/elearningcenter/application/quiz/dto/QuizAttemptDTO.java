package pl.dominik.elearningcenter.application.quiz.dto;

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
}
