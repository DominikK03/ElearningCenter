package pl.dominik.elearningcenter.application.quiz.input;

public record GetQuizDetailsInput(
        Long quizId,
        Long instructorId
) {
}
