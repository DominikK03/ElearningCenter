package pl.dominik.elearningcenter.application.quiz.dto;

import pl.dominik.elearningcenter.domain.quiz.Question;
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
    public static QuestionDTO from(Question question) {
        return new QuestionDTO(
                question.getId(),
                question.getText(),
                question.getType(),
                question.getPoints(),
                question.getOrderIndex(),
                question.getAnswers().stream()
                        .map(AnswerDTO::from)
                        .toList()
        );
    }

    public static QuestionDTO forStudent(Question question) {
        return new QuestionDTO(
                question.getId(),
                question.getText(),
                question.getType(),
                question.getPoints(),
                question.getOrderIndex(),
                question.getAnswers().stream()
                        .map(AnswerDTO::forStudent)
                        .toList()
        );
    }
}
