package pl.dominik.elearningcenter.application.quiz.dto;

import pl.dominik.elearningcenter.domain.quiz.valueobject.Answer;

public record AnswerDTO(
        String text,
        boolean correct
) {
    public static AnswerDTO from(Answer answer) {
        return new AnswerDTO(
                answer.getText(),
                answer.isCorrect()
        );
    }

    public static AnswerDTO forStudent(Answer answer) {
        return new AnswerDTO(
                answer.getText(),
                false
        );
    }
}
