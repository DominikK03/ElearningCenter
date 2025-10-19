package pl.dominik.elearningcenter.application.quiz.query;

public record GetStudentQuizAttemptsQuery(
        Long quizId,
        Long studentId
) {
}
