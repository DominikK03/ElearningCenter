package pl.dominik.elearningcenter.application.quiz.dto;

import java.util.List;

public record StudentAnswerDTO(
        Long questionId,
        List<Integer> selectedAnswerIndexes
) {
}
