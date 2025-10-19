package pl.dominik.elearningcenter.interfaces.rest.quiz.response;

import pl.dominik.elearningcenter.application.quiz.dto.QuestionDTO;
import pl.dominik.elearningcenter.domain.quiz.QuestionType;

import java.util.List;

public record QuestionResponse(
        Long id,
        String text,
        QuestionType type,
        int points,
        Integer orderIndex,
        List<AnswerResponse> answers
) {
    public static QuestionResponse from(QuestionDTO dto) {
        return new QuestionResponse(
                dto.id(),
                dto.text(),
                dto.type(),
                dto.points(),
                dto.orderIndex(),
                dto.answers().stream()
                        .map(AnswerResponse::from)
                        .toList()
        );
    }
}
