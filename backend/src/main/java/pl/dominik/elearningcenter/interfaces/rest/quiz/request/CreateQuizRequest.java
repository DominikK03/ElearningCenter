package pl.dominik.elearningcenter.interfaces.rest.quiz.request;

public record CreateQuizRequest(
        String title,
        int passingScore,
        Long lessonId
) {
}
