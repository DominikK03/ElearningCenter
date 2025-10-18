package pl.dominik.elearningcenter.application.quiz.input;

public record GetBestQuizAttemptInput(
        Long quizId,
        Long studentId
) {
}
