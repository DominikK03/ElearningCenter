package pl.dominik.elearningcenter.application.quiz.command;

public record DeleteQuestionCommand(
        Long quizId,
        Long questionId,
        Long instructorId
) {
}
