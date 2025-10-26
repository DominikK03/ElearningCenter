package pl.dominik.elearningcenter.application.quiz.command;

public record DeleteQuizCommand(
        Long quizId,
        Long instructorId
) {
}
