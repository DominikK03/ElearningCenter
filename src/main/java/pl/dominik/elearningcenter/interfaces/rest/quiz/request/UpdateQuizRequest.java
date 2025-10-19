package pl.dominik.elearningcenter.interfaces.rest.quiz.request;

public record UpdateQuizRequest(
        String title,
        int passingScore
) {
}
