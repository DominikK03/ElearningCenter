package pl.dominik.elearningcenter.application.quiz.dto;

import pl.dominik.elearningcenter.domain.quiz.valueobject.StudentAnswer;

import java.util.List;

public record StudentAnswerDTO(
        Long questionId,
        List<Integer> selectedAnswerIndexes
) {
    public static StudentAnswerDTO from(StudentAnswer studentAnswer) {
        return new StudentAnswerDTO(
                studentAnswer.getQuestionId(),
                studentAnswer.getSelectedAnswerIndexes()
        );
    }
}
