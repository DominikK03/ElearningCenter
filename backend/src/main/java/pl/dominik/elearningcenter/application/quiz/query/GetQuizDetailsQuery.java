package pl.dominik.elearningcenter.application.quiz.query;

public record GetQuizDetailsQuery(
        Long quizId,
        Long instructorId
) {
}
