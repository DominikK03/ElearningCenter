package pl.dominik.elearningcenter.interfaces.rest.quiz.request;

import pl.dominik.elearningcenter.domain.quiz.QuestionType;

import java.util.List;

public record AddQuestionRequest(
        String text,
        QuestionType type,
        int points,
        int orderIndex,
        List<AnswerRequest> answers
) {
    public record AnswerRequest(
            String text,
            boolean correct
    ) {}
}
