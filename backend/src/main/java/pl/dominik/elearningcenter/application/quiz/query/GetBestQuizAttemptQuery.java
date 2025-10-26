package pl.dominik.elearningcenter.application.quiz.query;

public record GetBestQuizAttemptQuery(
        Long quizId,
        Long studentId
) {
}
