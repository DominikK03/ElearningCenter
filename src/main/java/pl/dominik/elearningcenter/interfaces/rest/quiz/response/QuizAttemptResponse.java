package pl.dominik.elearningcenter.interfaces.rest.quiz.response;

import pl.dominik.elearningcenter.application.quiz.dto.QuizAttemptDTO;

import java.time.LocalDateTime;

public record QuizAttemptResponse(
        Long id,
        Long quizId,
        Long studentId,
        int score,
        int maxScore,
        boolean passed,
        int scorePercentage,
        LocalDateTime attemptedAt
) {
    public static QuizAttemptResponse from(QuizAttemptDTO dto) {
        return new QuizAttemptResponse(
                dto.id(),
                dto.quizId(),
                dto.studentId(),
                dto.score(),
                dto.maxScore(),
                dto.passed(),
                dto.scorePercentage(),
                dto.attemptedAt()
        );
    }
}
