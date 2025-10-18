package pl.dominik.elearningcenter.application.quiz.input;

public record CreateQuizInput(
        String title,
        int passingScore,
        Long lessonId,
        Long instructorId
) {
}
