package pl.dominik.elearningcenter.interfaces.rest.quiz.response;

import pl.dominik.elearningcenter.application.quiz.dto.AnswerDTO;

public record AnswerResponse(
        String text,
        boolean correct
) {
    public static AnswerResponse from(AnswerDTO dto) {
        return new AnswerResponse(
                dto.text(),
                dto.correct()
        );
    }
}
