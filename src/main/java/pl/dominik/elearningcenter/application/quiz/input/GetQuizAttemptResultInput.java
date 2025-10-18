package pl.dominik.elearningcenter.application.quiz.input;

public record GetQuizAttemptResultInput(
        Long attemptId,
        Long studentId
) {
}
