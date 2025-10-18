package pl.dominik.elearningcenter.application.quiz.input;

public record DeleteQuizInput(
        Long quizId,
        Long instructorId
) {
}
