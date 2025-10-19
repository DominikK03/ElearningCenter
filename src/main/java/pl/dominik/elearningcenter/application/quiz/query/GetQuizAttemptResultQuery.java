package pl.dominik.elearningcenter.application.quiz.query;

public record GetQuizAttemptResultQuery(
        Long attemptId,
        Long studentId
) {
}
