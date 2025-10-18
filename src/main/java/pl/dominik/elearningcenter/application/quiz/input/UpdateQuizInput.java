package pl.dominik.elearningcenter.application.quiz.input;

public record UpdateQuizInput(
        Long quizId,
        String title,
        int passingScore,
        Long instructorId
) {
}
