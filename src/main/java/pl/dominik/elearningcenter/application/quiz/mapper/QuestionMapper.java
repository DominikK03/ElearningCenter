package pl.dominik.elearningcenter.application.quiz.mapper;

import org.springframework.stereotype.Component;
import pl.dominik.elearningcenter.application.quiz.dto.QuestionDTO;
import pl.dominik.elearningcenter.domain.quiz.Question;

@Component
public class QuestionMapper {

    private final AnswerMapper answerMapper;

    public QuestionMapper(AnswerMapper answerMapper) {
        this.answerMapper = answerMapper;
    }

    public QuestionDTO toDto(Question question) {
        return new QuestionDTO(
                question.getId(),
                question.getText(),
                question.getType(),
                question.getPoints(),
                question.getOrderIndex(),
                question.getAnswers().stream()
                        .map(answerMapper::toDto)
                        .toList()
        );
    }

    public QuestionDTO toDtoForStudent(Question question) {
        return new QuestionDTO(
                question.getId(),
                question.getText(),
                question.getType(),
                question.getPoints(),
                question.getOrderIndex(),
                question.getAnswers().stream()
                        .map(answerMapper::toDtoForStudent)
                        .toList()
        );
    }
}
