package pl.dominik.elearningcenter.application.quiz.command;

public record UpdateQuizCommand(
        Long quizId,
        String title,
        int passingScore,
        Long instructorId
) {
}
