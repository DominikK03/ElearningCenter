package pl.dominik.elearningcenter.application.quiz.mapper;

import org.springframework.stereotype.Component;
import pl.dominik.elearningcenter.application.quiz.dto.AnswerDTO;
import pl.dominik.elearningcenter.domain.quiz.valueobject.Answer;

@Component
public class AnswerMapper {

    public AnswerDTO toDto(Answer answer, int index) {
        return new AnswerDTO(
                answer.getText(),
                answer.isCorrect(),
                index
        );
    }

    public AnswerDTO toDtoForStudent(Answer answer, int index) {
        return new AnswerDTO(
                answer.getText(),
                false,
                index
        );
    }
}
