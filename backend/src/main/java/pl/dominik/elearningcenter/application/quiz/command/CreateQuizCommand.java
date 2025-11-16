package pl.dominik.elearningcenter.application.quiz.command;

public record CreateQuizCommand(
        String title,
        int passingScore,
        Long courseId,
        Long sectionId,
        Long lessonId,
        Long instructorId
) {
}
