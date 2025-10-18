package pl.dominik.elearningcenter.application.quiz.input;

public record DeleteQuestionInput(
        Long quizId,
        Long questionId,
        Long instructorId
) {
}
