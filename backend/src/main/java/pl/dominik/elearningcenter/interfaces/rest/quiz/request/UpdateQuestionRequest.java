package pl.dominik.elearningcenter.interfaces.rest.quiz.request;

import java.util.List;

public record UpdateQuestionRequest(
        String text,
        Integer orderIndex,
        int points,
        List<AddQuestionRequest.AnswerRequest> answers
) {
}
