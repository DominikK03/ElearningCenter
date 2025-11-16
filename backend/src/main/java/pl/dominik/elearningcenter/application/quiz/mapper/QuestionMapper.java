package pl.dominik.elearningcenter.application.quiz.mapper;

import org.springframework.stereotype.Component;
import pl.dominik.elearningcenter.application.quiz.dto.AnswerDTO;
import pl.dominik.elearningcenter.application.quiz.dto.QuestionDTO;
import pl.dominik.elearningcenter.domain.quiz.Question;

import java.util.ArrayList;
import java.util.List;

@Component
public class QuestionMapper {

    private final AnswerMapper answerMapper;

    public QuestionMapper(AnswerMapper answerMapper) {
        this.answerMapper = answerMapper;
    }

    public QuestionDTO toDto(Question question) {
        List<AnswerDTO> answers = new ArrayList<>();
        for (int i = 0; i < question.getAnswers().size(); i++) {
            answers.add(answerMapper.toDto(question.getAnswers().get(i), i));
        }

        return new QuestionDTO(
                question.getId(),
                question.getText(),
                question.getType(),
                question.getPoints(),
                question.getOrderIndex(),
                answers
        );
    }

    public QuestionDTO toDtoForStudent(Question question) {
        List<AnswerDTO> answers = new ArrayList<>();
        for (int i = 0; i < question.getAnswers().size(); i++) {
            answers.add(answerMapper.toDtoForStudent(question.getAnswers().get(i), i));
        }

        return new QuestionDTO(
                question.getId(),
                question.getText(),
                question.getType(),
                question.getPoints(),
                question.getOrderIndex(),
                answers
        );
    }
}
