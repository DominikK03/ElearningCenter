package pl.dominik.elearningcenter.application.quiz.input;

public record GetStudentQuizAttemptsInput(
        Long quizId,
        Long studentId
) {
}
