package pl.dominik.elearningcenter.application.quiz.command;

public record CreateQuizCommand(
        String title,
        int passingScore,
        Long lessonId,
        Long instructorId
) {
}
