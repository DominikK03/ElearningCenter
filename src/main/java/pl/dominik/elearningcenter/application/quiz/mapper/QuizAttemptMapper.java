package pl.dominik.elearningcenter.application.quiz.mapper;

import org.springframework.stereotype.Component;
import pl.dominik.elearningcenter.application.quiz.dto.QuizAttemptDTO;
import pl.dominik.elearningcenter.domain.quiz.QuizAttempt;

@Component
public class QuizAttemptMapper {

    private final StudentAnswerMapper studentAnswerMapper;

    public QuizAttemptMapper(StudentAnswerMapper studentAnswerMapper) {
        this.studentAnswerMapper = studentAnswerMapper;
    }

    public QuizAttemptDTO toDto(QuizAttempt attempt) {
        return new QuizAttemptDTO(
                attempt.getId(),
                attempt.getQuizId(),
                attempt.getStudentId(),
                attempt.getScore(),
                attempt.getMaxScore(),
                attempt.getScorePercentage(),
                attempt.isPassed(),
                attempt.getAttemptedAt(),
                attempt.getAnswers().stream()
                        .map(studentAnswerMapper::toDto)
                        .toList()
        );
    }
}
