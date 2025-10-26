package pl.dominik.elearningcenter.application.quiz.dto;

import pl.dominik.elearningcenter.domain.quiz.QuestionType;

import java.util.List;

public record QuestionDTO(
        Long id,
        String text,
        QuestionType type,
        int points,
        Integer orderIndex,
        List<AnswerDTO> answers
) {
}
