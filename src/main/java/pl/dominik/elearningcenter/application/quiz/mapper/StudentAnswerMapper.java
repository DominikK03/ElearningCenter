package pl.dominik.elearningcenter.application.quiz.mapper;

import org.springframework.stereotype.Component;
import pl.dominik.elearningcenter.application.quiz.dto.StudentAnswerDTO;
import pl.dominik.elearningcenter.domain.quiz.valueobject.StudentAnswer;

@Component
public class StudentAnswerMapper {

    public StudentAnswerDTO toDto(StudentAnswer studentAnswer) {
        return new StudentAnswerDTO(
                studentAnswer.getQuestionId(),
                studentAnswer.getSelectedAnswerIndexes()
        );
    }
}
